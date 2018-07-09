/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.google.gson.Gson;

public class CLangExtractor {
    public static final String DEPS_MK_FILENAME_PATTERN = "deps_%s_%d.mk";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Set<File> processedDependencyFiles = new HashSet<>(200);
    private final Set<PackageDetails> processedDependencies = new HashSet<>(40);

    private final ExternalIdFactory externalIdFactory;
    private final ExecutableRunner executableRunner;
    private final CLangDependenciesListFileManager dependencyFileManager;
    private final DetectFileManager detectFileManager;

    public CLangExtractor(final ExternalIdFactory externalIdFactory, final ExecutableRunner executableRunner,
            final DetectFileManager detectFileManager, final CLangDependenciesListFileManager dependencyFileManager) {
        this.externalIdFactory = externalIdFactory;
        this.executableRunner = executableRunner;
        this.detectFileManager = detectFileManager;
        this.dependencyFileManager = dependencyFileManager;
    }

    public Extraction extract(final LinuxPackageManager pkgMgr, final File givenDir, final int depth, final ExtractionId extractionId, final File jsonCompilationDatabaseFile) {
        try {
            logger.info(String.format("Analyzing %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            final File rootDir = getRootDir(givenDir, depth);
            final File outputDirectory = detectFileManager.getOutputDirectory("CLang", extractionId);
            logger.debug(String.format("extract() called; compileCommandsJsonFilePath: %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            final Set<File> filesForIScan = ConcurrentHashMap.newKeySet(64);
            final List<CompileCommand> compileCommands = parseCompileCommandsFile(jsonCompilationDatabaseFile);
            final List<Dependency> bdioComponents = compileCommands.parallelStream()
                    .map(compileCommandToDependencyFilePathsConverter(outputDirectory))
                    .reduce(ConcurrentHashMap.newKeySet(), pathsAccumulator()).parallelStream()
                    .filter((final String path) -> StringUtils.isNotBlank(path))
                    .map((final String path) -> new File(path))
                    .filter(fileIsNewPredicate())
                    .map(fileToPackagesConverter(rootDir, filesForIScan, pkgMgr))
                    .reduce(ConcurrentHashMap.newKeySet(), packageAccumulator()).parallelStream()
                    .map(packageToDependenciesConverter(pkgMgr))
                    .reduce(new ArrayList<Dependency>(), dependenciesAccumulator());
            final MutableDependencyGraph dependencyGraph = populateGraph(bdioComponents);
            final ExternalId externalId = externalIdFactory.createPathExternalId(pkgMgr.getDefaultForge(), rootDir.toString());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolGroupType.CLANG, BomToolType.CLANG, rootDir.toString(), externalId, dependencyGraph).build();
            logFilesForIScan(filesForIScan);
            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private void logFilesForIScan(final Set<File> filesForIScan) {
        logger.info(String.format("Number of dependency files not recognized by the package manager: %d", filesForIScan.size()));
        if (logger.isDebugEnabled()) {
            for (final File unMatchedDependencyFile : filesForIScan) {
                logger.info(String.format("\tDependency file not recognized by the package manager: %s", unMatchedDependencyFile.getAbsolutePath()));
            }
        }
    }

    private BinaryOperator<List<Dependency>> dependenciesAccumulator() {
        final BinaryOperator<List<Dependency>> accumulateNewDependencies = (dependenciesAccumulator, newlyDiscoveredDependencies) -> {
            dependenciesAccumulator.addAll(newlyDiscoveredDependencies);
            return dependenciesAccumulator;
        };
        return accumulateNewDependencies;
    }

    private Function<PackageDetails, List<Dependency>> packageToDependenciesConverter(final LinuxPackageManager pkgMgr) {
        final Function<PackageDetails, List<Dependency>> convertPackageToDependencies = (final PackageDetails pkg) -> {
            final List<Dependency> dependencies = new ArrayList<>();
            logger.debug(String.format("Package name//arch//version: %s//%s//%s", pkg.getPackageName(), pkg.getPackageArch(),
                    pkg.getPackageVersion()));
            if (dependencyAlreadyProcessed(pkg)) {
                logger.trace(String.format("dependency %s has already been processed", pkg.toString()));
            } else if (pkg.getPackageName() != null && pkg.getPackageVersion() != null && pkg.getPackageArch() != null) {
                dependencies.addAll(getBdioComponents(pkgMgr, pkg.getPackageName(), pkg.getPackageVersion(), pkg.getPackageArch()));
            }
            return dependencies;
        };
        return convertPackageToDependencies;
    }

    private BinaryOperator<Set<PackageDetails>> packageAccumulator() {
        final BinaryOperator<Set<PackageDetails>> accumulateNewPackages = (allPackages, newPackages) -> {
            allPackages.addAll(newPackages);
            return allPackages;
        };
        return accumulateNewPackages;
    }

    private Function<File, Set<PackageDetails>> fileToPackagesConverter(final File sourceDir, final Set<File> filesForIScan, final LinuxPackageManager pkgMgr) {
        final Function<File, Set<PackageDetails>> convertFileToPackages = (final File f) -> {
            logger.trace(String.format("Querying package manager for %s", f.getAbsolutePath()));
            final DependencyDetails dependencyFileWithMetaData = new DependencyDetails(isUnder(sourceDir, f) ? true : false, f);
            final Set<PackageDetails> packages = new HashSet<>(pkgMgr.getPackages(executableRunner, filesForIScan, dependencyFileWithMetaData));
            logger.debug(String.format("Found %d packages for %s", packages.size(), f.getAbsolutePath()));
            return packages;
        };
        return convertFileToPackages;
    }

    private Predicate<File> fileIsNewPredicate() {
        final Predicate<File> fileIsNew = (final File dependencyFile) -> {
            if (dependencyFileAlreadyProcessed(dependencyFile)) {
                logger.trace(String.format("Dependency file %s has already been processed; excluding it", dependencyFile.getAbsolutePath()));
                return false;
            }
            if (!dependencyFile.exists()) {
                logger.debug(String.format("Dependency file %s does NOT exist; excluding it", dependencyFile.getAbsolutePath()));
                return false;
            }
            logger.trace(String.format("Dependency file %s does exist; including it", dependencyFile.getAbsolutePath()));
            return true;
        };
        return fileIsNew;
    }

    private BinaryOperator<Set<String>> pathsAccumulator() {
        final BinaryOperator<Set<String>> accumulateNewPaths = (allPaths, newPaths) -> {
            allPaths.addAll(newPaths);
            return allPaths;
        };
        return accumulateNewPaths;
    }

    private Function<CompileCommand, Set<String>> compileCommandToDependencyFilePathsConverter(final File workingDir) {
        final Function<CompileCommand, Set<String>> convertCompileCommandToDependencyFilePaths = (final CompileCommand compileCommand) -> {
            logger.info(String.format("Analyzing source file: %s", compileCommand.file));
            final Set<String> dependencyFilePaths = new HashSet<>();
            final Optional<File> depsMkFile = generateDependencyFileByCompiling(workingDir, compileCommand);
            dependencyFilePaths.addAll(dependencyFileManager.parse(depsMkFile.orElse(null)));
            dependencyFileManager.remove(depsMkFile.orElse(null));
            return dependencyFilePaths;
        };
        return convertCompileCommandToDependencyFilePaths;
    }

    private MutableDependencyGraph populateGraph(final List<Dependency> bdioComponents) {
        final MutableDependencyGraph dependencyGraph = new SimpleBdioFactory().createMutableDependencyGraph();
        for (final Dependency bdioComponent : bdioComponents) {
            dependencyGraph.addChildToRoot(bdioComponent);
        }
        return dependencyGraph;
    }

    private List<Dependency> getBdioComponents(final LinuxPackageManager pkgMgr, final String name, final String version, final String arch) {
        final List<Dependency> dependencies = new ArrayList<>();
        final String externalId = String.format("%s/%s/%s", name, version, arch);
        logger.trace(String.format("Constructed externalId: %s", externalId));
        for (final Forge forge : pkgMgr.getForges()) {
            final ExternalId extId = new SimpleBdioFactory().createArchitectureExternalId(forge, name, version, arch);
            final Dependency dep = new SimpleBdioFactory().createDependency(name, version, extId);
            logger.debug(String.format("forge: %s: adding %s version %s as child to dependency node tree; externalId: %s", forge.getName(), dep.name, dep.version, dep.externalId.createBdioId()));
            dependencies.add(dep);
        }
        return dependencies;
    }

    private List<CompileCommand> parseCompileCommandsFile(final File compileCommandsJsonFile) throws IOException {
        final String compileCommandsJson = FileUtils.readFileToString(compileCommandsJsonFile, StandardCharsets.UTF_8);
        final Gson gson = new Gson();
        final CompileCommand[] compileCommands = gson.fromJson(compileCommandsJson, CompileCommand[].class);
        return Arrays.asList(compileCommands);
    }

    private Optional<File> generateDependencyFileByCompiling(final File workingDir,
            final CompileCommand compileCommand) {

        final int randomInt = (int) (Math.random() * 1000);
        final String sourceFilenameBase = getFilenameBase(compileCommand.file);
        final String depsMkFilename = String.format(DEPS_MK_FILENAME_PATTERN, sourceFilenameBase, randomInt);
        final File depsMkFile = new File(workingDir, depsMkFilename);
        try {
            executableRunner.executeFromDirQuietly(new File(compileCommand.directory), getCompilerCommand(compileCommand.command),
                    getArgsForGeneratingDepsMkFile(compileCommand.command, depsMkFile.getAbsolutePath()));
        } catch (final ExecutableRunnerException e) {
            logger.debug(String.format("Error generating dependencies file for command '%s': %s", compileCommand.command, e.getMessage()));
            return Optional.empty();
        }
        return Optional.of(depsMkFile);
    }

    private String getCompilerCommand(final String origCompileCommand) {
        final String[] parts = origCompileCommand.trim().split("\\s+");
        return parts[0];
    }

    private List<String> getArgsForGeneratingDepsMkFile(final String origCompileCommand, final String depsMkFilePath) {
        final String[] parts = origCompileCommand.trim().split("\\s+");
        final List<String> argList = new ArrayList<>(parts.length + 3);
        int partIndex = 0;
        for (final String part : parts) {
            if (partIndex > 0) {
                argList.add(part);
            }
            partIndex++;
        }
        argList.add("-M");
        argList.add("-MF");
        argList.add(depsMkFilePath);
        return argList;
    }

    private String getFilenameBase(final String filePathString) {
        final Path filePath = new File(filePathString).toPath();
        return FilenameUtils.removeExtension(filePath.getFileName().toString());
    }

    private File getRootDir(final File givenDir, int depth) {
        logger.debug(String.format("givenDir: %s; depth: %d", givenDir, depth));
        File rootDir = givenDir;
        for (; depth > 0; depth--) {
            rootDir = rootDir.getParentFile();
        }
        logger.debug(String.format("rootDir: %s", rootDir));
        return rootDir;
    }

    private boolean isUnder(final File dir, final File file) {
        logger.trace(String.format("Checking to see if file %s is under dir %s", file.getAbsolutePath(), dir.getAbsolutePath()));
        try {
            final String dirPath = dir.getCanonicalPath();
            final String filePath = file.getCanonicalPath();
            logger.trace(String.format("\tactually comparing file path %s with dir path %s", filePath, dirPath));
            if (filePath.equals(dirPath) || filePath.startsWith(dirPath)) {
                logger.trace(String.format("\t%s is under %s", file.getAbsolutePath(), dir.getAbsolutePath()));
                return true;
            }
            logger.trace(String.format("\t%s is not under %s", file.getAbsolutePath(), dir.getAbsolutePath()));
            return false;
        } catch (final IOException e) {
            logger.warn(String.format("Error getting canonical path for either %s or %s", dir.getAbsolutePath(), file.getAbsolutePath()));
            return false;
        }
    }

    private boolean dependencyFileAlreadyProcessed(final File dependencyFile) {
        if (processedDependencyFiles.contains(dependencyFile)) {
            return true;
        }
        processedDependencyFiles.add(dependencyFile);
        return false;
    }

    private boolean dependencyAlreadyProcessed(final PackageDetails dependency) {
        if (processedDependencies.contains(dependency)) {
            return true;
        }
        processedDependencies.add(dependency);
        return false;
    }
}
