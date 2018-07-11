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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class CLangExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Set<File> processedDependencyFiles = new HashSet<>(200);
    private final Set<PackageDetails> processedDependencies = new HashSet<>(40);

    private final ExecutableRunner executableRunner;
    private final DependenciesListFileManager dependenciesListFileManager;
    private final DetectFileManager detectFileManager;
    private final CompileCommandsJsonFileParser compileCommandsJsonFileParser;
    private final CodeLocationAssembler codeLocationAssembler;

    public CLangExtractor(final ExecutableRunner executableRunner,
            final DetectFileManager detectFileManager, final DependenciesListFileManager dependenciesListFileManager,
            final CompileCommandsJsonFileParser compileCommandsJsonFileParser, final CodeLocationAssembler codeLocationAssembler) {
        this.executableRunner = executableRunner;
        this.detectFileManager = detectFileManager;
        this.dependenciesListFileManager = dependenciesListFileManager;
        this.compileCommandsJsonFileParser = compileCommandsJsonFileParser;
        this.codeLocationAssembler = codeLocationAssembler;
    }

    public Extraction extract(final LinuxPackageManager pkgMgr, final File givenDir, final int depth, final ExtractionId extractionId, final File jsonCompilationDatabaseFile) {
        try {
            logger.info(String.format("Analyzing %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            final File rootDir = FileUtils.getRootDir(givenDir, depth);
            final File outputDirectory = detectFileManager.getOutputDirectory("CLang", extractionId);
            logger.debug(String.format("extract() called; compileCommandsJsonFilePath: %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            final Set<File> filesForIScan = ConcurrentHashMap.newKeySet(64);
            final List<CompileCommand> compileCommands = compileCommandsJsonFileParser.parse(jsonCompilationDatabaseFile);
            final List<Dependency> bdioComponents = compileCommands.parallelStream()
                    .flatMap(compileCommandToDependencyFilePathsConverter(outputDirectory))
                    .collect(Collectors.toSet()).parallelStream()
                    .filter((final String path) -> StringUtils.isNotBlank(path))
                    .map((final String path) -> new File(path))
                    .filter(fileIsNewPredicate())
                    .flatMap(fileToPackagesConverter(rootDir, filesForIScan, pkgMgr))
                    .collect(Collectors.toSet()).parallelStream()
                    .flatMap(packageToDependenciesConverter(pkgMgr))
                    .collect(Collectors.toList());

            final DetectCodeLocation detectCodeLocation = codeLocationAssembler.generateCodeLocation(pkgMgr.getDefaultForge(), rootDir, bdioComponents);
            logSummary(bdioComponents, filesForIScan);
            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Function<PackageDetails, Stream<Dependency>> packageToDependenciesConverter(final LinuxPackageManager pkgMgr) {
        final Function<PackageDetails, Stream<Dependency>> convertPackageToDependencies = (final PackageDetails pkg) -> {
            final List<Dependency> dependencies = new ArrayList<>();
            logger.debug(String.format("Package name//arch//version: %s//%s//%s", pkg.getPackageName(), pkg.getPackageArch(),
                    pkg.getPackageVersion()));
            if (dependencyAlreadyProcessed(pkg)) {
                logger.trace(String.format("dependency %s has already been processed", pkg.toString()));
            } else if (pkg.getPackageName() != null && pkg.getPackageVersion() != null && pkg.getPackageArch() != null) {
                dependencies.addAll(getDependencies(pkgMgr, pkg.getPackageName(), pkg.getPackageVersion(), pkg.getPackageArch()));
            }
            return dependencies.stream();
        };
        return convertPackageToDependencies;
    }

    private Function<File, Stream<PackageDetails>> fileToPackagesConverter(final File sourceDir, final Set<File> filesForIScan, final LinuxPackageManager pkgMgr) {
        final Function<File, Stream<PackageDetails>> convertFileToPackages = (final File f) -> {
            logger.trace(String.format("Querying package manager for %s", f.getAbsolutePath()));
            final DependencyFileDetails dependencyFileWithMetaData = new DependencyFileDetails(FileUtils.isUnder(sourceDir, f) ? true : false, f);
            final Set<PackageDetails> packages = new HashSet<>(pkgMgr.getPackages(executableRunner, filesForIScan, dependencyFileWithMetaData));
            logger.debug(String.format("Found %d packages for %s", packages.size(), f.getAbsolutePath()));
            return packages.stream();
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

    private Function<CompileCommand, Stream<String>> compileCommandToDependencyFilePathsConverter(final File workingDir) {
        final Function<CompileCommand, Stream<String>> convertCompileCommandToDependencyFilePaths = (final CompileCommand compileCommand) -> {
            logger.info(String.format("Analyzing source file: %s", compileCommand.file));
            final Set<String> dependencyFilePaths = dependenciesListFileManager.generateDependencyFilePaths(workingDir, compileCommand);
            return dependencyFilePaths.stream();
        };
        return convertCompileCommandToDependencyFilePaths;
    }

    private List<Dependency> getDependencies(final LinuxPackageManager pkgMgr, final String name, final String version, final String arch) {
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

    private void logSummary(final List<Dependency> bdioComponents, final Set<File> filesForIScan) {
        logger.info(String.format("Number of unique component external IDs generated: %d", bdioComponents.size()));
        if (logger.isDebugEnabled()) {
            for (final Dependency bdioComponent : bdioComponents) {
                logger.info(String.format("\tComponent: %s", bdioComponent.externalId));
            }
        }
        logger.info(String.format("Number of dependency files not recognized by the package manager: %d", filesForIScan.size()));
        if (logger.isDebugEnabled()) {
            for (final File unMatchedDependencyFile : filesForIScan) {
                logger.info(String.format("\tDependency file not recognized by the package manager: %s", unMatchedDependencyFile.getAbsolutePath()));
            }
        }
    }
}
