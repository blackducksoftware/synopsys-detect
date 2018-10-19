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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.google.gson.Gson;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;

public class ClangExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Set<File> processedDependencyFiles = new HashSet<>(200);
    private final Set<PackageDetails> processedDependencies = new HashSet<>(40);

    private final ExecutableRunner executableRunner;
    private final Gson gson;
    private final DetectFileFinder fileFinder;
    private final DependenciesListFileManager dependenciesListFileManager;
    private final DetectFileManager detectFileManager;
    private final CodeLocationAssembler codeLocationAssembler;
    private final SimpleBdioFactory bdioFactory;

    public ClangExtractor(final ExecutableRunner executableRunner, final Gson gson, final DetectFileFinder fileFinder,
            final DetectFileManager detectFileManager, final DependenciesListFileManager dependenciesListFileManager,
            final CodeLocationAssembler codeLocationAssembler) {
        this.executableRunner = executableRunner;
        this.gson = gson;
        this.fileFinder = fileFinder;
        this.detectFileManager = detectFileManager;
        this.dependenciesListFileManager = dependenciesListFileManager;
        this.codeLocationAssembler = codeLocationAssembler;
        this.bdioFactory = new SimpleBdioFactory();
    }

    public Extraction extract(final ClangLinuxPackageManager pkgMgr, final File givenDir, final int depth, final ExtractionId extractionId, final File jsonCompilationDatabaseFile) {
        try {
            logger.info(String.format("Analyzing %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            final File rootDir = fileFinder.findContainingDir(givenDir, depth);
            final File outputDirectory = detectFileManager.getOutputDirectory(extractionId);
            logger.debug(String.format("extract() called; compileCommandsJsonFilePath: %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            final Set<File> unManagedDependencyFiles = ConcurrentHashMap.newKeySet(64);
            final List<CompileCommandWrapper> compileCommands = parseJsonCompilationDatabaseFile(jsonCompilationDatabaseFile);
            final List<Dependency> bdioComponents = compileCommands.parallelStream()
                    .flatMap(compileCommandToDependencyFilePathsConverter(outputDirectory))
                    .collect(Collectors.toSet()).parallelStream()
                    .filter(StringUtils::isNotBlank)
                    .map(File::new)
                    .filter(fileIsNewPredicate())
                    .flatMap(dependencyFileToLinuxPackagesConverter(rootDir, unManagedDependencyFiles, pkgMgr))
                    .collect(Collectors.toSet()).parallelStream()
                    .flatMap(linuxPackageToBdioComponentsConverter(pkgMgr))
                    .collect(Collectors.toList());

            final DetectCodeLocation detectCodeLocation = codeLocationAssembler.generateCodeLocation(pkgMgr.getDefaultForge(), rootDir, bdioComponents);
            logSummary(bdioComponents, unManagedDependencyFiles);
            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Function<CompileCommandWrapper, Stream<String>> compileCommandToDependencyFilePathsConverter(final File workingDir) {
        return (final CompileCommandWrapper compileCommand) -> {
            logger.info(String.format("Analyzing source file: %s", compileCommand.getFile()));
            final Set<String> dependencyFilePaths = dependenciesListFileManager.generateDependencyFilePaths(workingDir, compileCommand);
            return dependencyFilePaths.stream();
        };
    }

    private Predicate<File> fileIsNewPredicate() {
        return (final File dependencyFile) -> {
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
    }

    private Function<File, Stream<PackageDetails>> dependencyFileToLinuxPackagesConverter(final File sourceDir, final Set<File> unManagedDependencyFiles, final ClangLinuxPackageManager pkgMgr) {
        return (final File f) -> {
            logger.trace(String.format("Querying package manager for %s", f.getAbsolutePath()));
            final DependencyFileDetails dependencyFileWithMetaData = new DependencyFileDetails(fileFinder.isFileUnderDir(sourceDir, f), f);
            final Set<PackageDetails> linuxPackages = new HashSet<>(pkgMgr.getPackages(executableRunner, unManagedDependencyFiles, dependencyFileWithMetaData));
            logger.debug(String.format("Found %d packages for %s", linuxPackages.size(), f.getAbsolutePath()));
            if (linuxPackages.size() == 0 && !dependencyFileWithMetaData.isInBuildDir()) {
                logger.info(String.format("Adding %s to unManagedDependencyFiles", f.getAbsolutePath()));
                unManagedDependencyFiles.add(f);
            }
            return linuxPackages.stream();
        };
    }

    private Function<PackageDetails, Stream<Dependency>> linuxPackageToBdioComponentsConverter(final ClangLinuxPackageManager pkgMgr) {
        return (final PackageDetails pkg) -> {
            final List<Dependency> bdioComponents = new ArrayList<>();
            logger.debug(String.format("Package name//arch//version: %s//%s//%s", pkg.getPackageName(), pkg.getPackageArch(),
                    pkg.getPackageVersion()));
            if (dependencyAlreadyProcessed(pkg)) {
                logger.trace(String.format("dependency %s has already been processed", pkg.toString()));
            } else if (pkg.getPackageName() != null && pkg.getPackageVersion() != null && pkg.getPackageArch() != null) {
                bdioComponents.addAll(getBdioComponents(pkgMgr, pkg.getPackageName(), pkg.getPackageVersion(), pkg.getPackageArch()));
            }
            return bdioComponents.stream();
        };
    }

    private List<Dependency> getBdioComponents(final ClangLinuxPackageManager pkgMgr, final String name, final String version, final String arch) {
        final List<Dependency> dependencies = new ArrayList<>();
        final String externalId = String.format("%s/%s/%s", name, version, arch);
        logger.trace(String.format("Constructed externalId: %s", externalId));
        for (final Forge forge : pkgMgr.getForges()) {
            final ExternalId extId = bdioFactory.createArchitectureExternalId(forge, name, version, arch);
            final Dependency dep = bdioFactory.createDependency(name, version, extId);
            logger.debug(String.format("forge: %s: adding %s version %s as child to dependency node tree; externalId: %s", forge.getName(), dep.name, dep.version, dep.externalId.createBdioId()));
            dependencies.add(dep);
        }
        return dependencies;
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

    public List<CompileCommandWrapper> parseJsonCompilationDatabaseFile(final File compileCommandsJsonFile) throws IOException {
        final String compileCommandsJson = FileUtils.readFileToString(compileCommandsJsonFile, StandardCharsets.UTF_8);
        final CompileCommand[] compileCommands = gson.fromJson(compileCommandsJson, CompileCommand[].class);
        return Arrays.stream(compileCommands).map(rawCommand -> new CompileCommandWrapper(rawCommand)).collect(Collectors.toList());
    }

    private void logSummary(final List<Dependency> bdioComponents, final Set<File> unManagedDependencyFiles) {
        logger.info(String.format("Number of unique component external IDs generated: %d", bdioComponents.size()));
        if (logger.isDebugEnabled()) {
            for (final Dependency bdioComponent : bdioComponents) {
                logger.info(String.format("\tComponent: %s", bdioComponent.externalId));
            }
        }
        logger.info("Dependency files outside the build directory that were not recognized by the package manager:");
        for (final File unMatchedDependencyFile : unManagedDependencyFiles) {
            logger.info(String.format("\t%s", unMatchedDependencyFile.getAbsolutePath()));
        }

    }
}
