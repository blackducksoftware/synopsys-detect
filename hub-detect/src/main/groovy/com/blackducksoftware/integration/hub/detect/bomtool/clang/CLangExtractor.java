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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.bdio.BdioTransformer;
import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.executor.Executor;
import com.blackducksoftware.integration.hub.detect.bomtool.clang.pkgmgr.PkgMgr;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.google.gson.Gson;

@Component
public class CLangExtractor {
    private static final String COMPILE_CMD_PATTERN_WITH_DEPENDENCY_OUTPUT_FILE = "%s -M -MF %s";
    public static final String DEPS_MK_FILENAME_PATTERN = "deps_%s_%d.mk";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Set<File> processedDependencyFiles = new HashSet<>(200);
    private final Set<PackageDetails> processedDependencies = new HashSet<>(40);

    @Autowired
    private List<PkgMgr> pkgMgrs;

    @Autowired
    BdioTransformer bdioTransformer;

    @Autowired
    ExternalIdFactory externalIdFactory;

    @Autowired
    Executor executor;

    @Autowired
    DependencyFileManager dependencyFileManager;

    @Autowired
    DetectFileManager detectFileManager;

    // TODO: Could skip the creation of SimpleBdioDocument; maybe next 2 methods should be combined
    public Extraction extract(final File sourceDir, final ExtractionId extractionId, final File jsonCompilationDatabaseFile) {
        try {
            logger.info(String.format("Analyzing %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            final File outputDirectory = detectFileManager.getOutputDirectory(extractionId);
            final ExtractorResults results = extract(sourceDir, executor, dependencyFileManager, jsonCompilationDatabaseFile, outputDirectory);
            final SimpleBdioDocument simpleBdioDocument = results.getBdioDocument();

            final DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(simpleBdioDocument.project, simpleBdioDocument.components);
            final Forge forgeFromName = Forge.FORGE_NAME_TO_FORGE.get(simpleBdioDocument.project.bdioExternalIdentifier.forge);
            final ExternalId externalId = externalIdFactory.createPathExternalId(forgeFromName, sourceDir.toString());
            final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(BomToolGroupType.CLANG, sourceDir.toString(), externalId, dependencyGraph).build();
            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    ExtractorResults extract(final File sourceDir, final Executor executor, final DependencyFileManager dependencyFileManager, final File compileCommandsJsonFile, final File workingDir)
            throws IOException, ExecutableRunnerException, IntegrationException {
        logger.debug(String.format("extract() called; compileCommandsJsonFilePath: %s", compileCommandsJsonFile.getAbsolutePath()));
        final Set<File> filesForIScan = ConcurrentHashMap.newKeySet(64);
        final PkgMgr pkgMgr = selectPkgMgr(executor);
        final ExternalId projectExternalId = new SimpleBdioFactory().createNameVersionExternalId(pkgMgr.getDefaultForge(), null, null);
        final SimpleBdioDocument bdioDocument = new SimpleBdioFactory().createSimpleBdioDocument(null, null, null, projectExternalId);
        final MutableDependencyGraph dependencyGraph = new SimpleBdioFactory().createMutableDependencyGraph();
        final List<CompileCommand> compileCommands = parseCompileCommandsFile(compileCommandsJsonFile);

        final List<Dependency> bdioComponents = compileCommands.parallelStream()
                .map(geConvertCompileCommandToDependencyFilePathsFunction(executor, dependencyFileManager, workingDir))
                .reduce(new HashSet<>(), getStringsAccumulator()).parallelStream() // TODO: flatMap seems ok here
                .filter((final String path) -> !StringUtils.isBlank(path))
                .map((final String path) -> new File(path))
                .filter(getFileIsNewPredicate())
                .map(getConvertFileToPackagesFunction(sourceDir, executor, filesForIScan, pkgMgr))
                .reduce(new HashSet<>(), getPackageAccumulator()).parallelStream() // TODO: flatMap totally breaks it here
                .map(getConvertPackageToDependenciesFunction(pkgMgr))
                .reduce(new ArrayList<Dependency>(), getDependenciesAccumulator()); // TODO: Collector
        for (final Dependency bdioComponent : bdioComponents) {
            logger.debug(String.format("bdioComponent: %s", bdioComponent.externalId));
        }

        populateGraph(dependencyGraph, bdioComponents);
        new SimpleBdioFactory().populateComponents(bdioDocument, projectExternalId, dependencyGraph);
        return new ExtractorResults(bdioDocument, filesForIScan);
    }

    private BinaryOperator<List<Dependency>> getDependenciesAccumulator() {
        final BinaryOperator<List<Dependency>> accumulateNewDependencies = (dependenciesAccumulator, newlyDiscoveredDependencies) -> {
            dependenciesAccumulator.addAll(newlyDiscoveredDependencies);
            return dependenciesAccumulator;
        };
        return accumulateNewDependencies;
    }

    private Function<PackageDetails, List<Dependency>> getConvertPackageToDependenciesFunction(final PkgMgr pkgMgr) {
        final Function<PackageDetails, List<Dependency>> convertPackageToDependencies = (final PackageDetails pkg) -> {
            final List<Dependency> dependencies = new ArrayList<>();
            logger.debug(String.format("Package name//arch//version: %s//%s//%s", pkg.getPackageName().orElse("<missing>"), pkg.getPackageArch().orElse("<missing>"),
                    pkg.getPackageVersion().orElse("<missing>")));
            if (dependencyAlreadyProcessed(pkg)) {
                logger.trace(String.format("dependency %s has already been processed", pkg.toString()));
            } else if (pkg.getPackageName().isPresent() && pkg.getPackageVersion().isPresent() && pkg.getPackageArch().isPresent()) {
                dependencies.addAll(getBdioComponents(pkgMgr, pkg.getPackageName().get(), pkg.getPackageVersion().get(), pkg.getPackageArch().get()));
            }
            return dependencies;
        };
        return convertPackageToDependencies;
    }

    private BinaryOperator<Set<PackageDetails>> getPackageAccumulator() {
        final BinaryOperator<Set<PackageDetails>> accumulateNewPackages = (packagesAccumulator, newlyDiscoveredPackages) -> {
            packagesAccumulator.addAll(newlyDiscoveredPackages);
            return packagesAccumulator;
        };
        return accumulateNewPackages;
    }

    private Function<File, Set<PackageDetails>> getConvertFileToPackagesFunction(final File sourceDir, final Executor executor, final Set<File> filesForIScan, final PkgMgr pkgMgr) {
        final Function<File, Set<PackageDetails>> convertFileToPackages = (final File f) -> {
            final DependencyFile dependencyFileWrapper = new DependencyFile(isUnder(sourceDir, f) ? true : false, f);
            return new HashSet<>(pkgMgr.getDependencyDetails(executor, filesForIScan, dependencyFileWrapper));
        };
        return convertFileToPackages;
    }

    private Predicate<File> getFileIsNewPredicate() {
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

    private BinaryOperator<Set<String>> getStringsAccumulator() {
        final BinaryOperator<Set<String>> accumulateNewPaths = (pathsAccumulator, newlyDiscoveredPaths) -> {
            pathsAccumulator.addAll(newlyDiscoveredPaths);
            return pathsAccumulator;
        };
        return accumulateNewPaths;
    }

    private Function<CompileCommand, Set<String>> geConvertCompileCommandToDependencyFilePathsFunction(final Executor executor, final DependencyFileManager dependencyFileManager, final File workingDir) {
        final Function<CompileCommand, Set<String>> convertCompileCommandToDependencyFilePaths = (final CompileCommand compileCommand) -> {
            final Set<String> dependencyFilePaths = new HashSet<>();
            final Optional<File> depsMkFile = generateDependencyFileByCompiling(executor, workingDir, compileCommand);
            dependencyFilePaths.addAll(dependencyFileManager.parse(depsMkFile));
            dependencyFileManager.remove(depsMkFile);
            return dependencyFilePaths;
        };
        return convertCompileCommandToDependencyFilePaths;
    }

    private void populateGraph(final MutableDependencyGraph graph, final List<Dependency> bdioComponents) {
        for (final Dependency bdioComponent : bdioComponents) {
            graph.addChildToRoot(bdioComponent);
        }
    }

    private List<Dependency> getBdioComponents(final PkgMgr pkgMgr, final String name, final String version, final String arch) {
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

    private PkgMgr selectPkgMgr(final Executor executor) throws IntegrationException {
        PkgMgr pkgMgr = null;
        for (final PkgMgr pkgMgrCandidate : pkgMgrs) {
            if (pkgMgrCandidate.applies(executor)) {
                pkgMgr = pkgMgrCandidate;
                break;
            }
        }
        if (pkgMgr == null) {
            throw new IntegrationException("Unable to execute any supported package manager; Please make sure that one of the supported package managers is on the PATH");
        }
        return pkgMgr;
    }

    private Optional<File> generateDependencyFileByCompiling(final Executor executor, final File workingDir,
            final CompileCommand compileCommand) {

        final int randomInt = (int) (Math.random() * 1000);
        final String sourceFilenameBase = getFilenameBase(compileCommand.file);
        final String depsMkFilename = String.format(DEPS_MK_FILENAME_PATTERN, sourceFilenameBase, randomInt);
        final File depsMkFile = new File(workingDir, depsMkFilename);
        final String generateDependenciesFileCommand = String.format(COMPILE_CMD_PATTERN_WITH_DEPENDENCY_OUTPUT_FILE, compileCommand.command, depsMkFile.getAbsolutePath());
        try {
            executor.execute(new File(compileCommand.directory), null, generateDependenciesFileCommand);
        } catch (ExecutableRunnerException | IntegrationException e) {
            logger.debug(String.format("Error compiling with command '%s': %s", generateDependenciesFileCommand, e.getMessage()));
            return Optional.empty();
        }
        return Optional.of(depsMkFile);
    }

    // TODO belongs elsewhere?
    private String getFilenameBase(final String filePath) {
        final File f = new File(filePath);
        final Path p = f.toPath();
        final String filename = p.getFileName().toString();
        if (!filename.contains(".")) {
            return filename;
        }
        final int dotIndex = filename.indexOf('.');
        return filename.substring(0, dotIndex);
    }

    private boolean isUnder(final File dir, final File file) {
        logger.trace(String.format("Checking to see if file %s is under dir %s", file.getAbsolutePath(), dir.getAbsolutePath()));
        try {
            final String dirPath = dir.getCanonicalPath();
            final String filePath = file.getCanonicalPath();
            logger.trace(String.format("\tactually comparing file path %s with dir path %s", filePath, dirPath));
            if (filePath.equals(dirPath) || filePath.startsWith(dirPath)) {
                logger.trace("it is");
                return true;
            }
            logger.trace("it is not");
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
