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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
            final Set<String> dependencyPaths = compileCommands.parallelStream()
                    .flatMap(command -> convertCompileCommandToDependencyFilePaths(command, outputDirectory).stream())
                    .collect(Collectors.toSet());
            final Set<File> dependencyFiles = dependencyPaths.parallelStream()
                    .filter(path -> StringUtils.isNotBlank(path))
                    .map(path -> new File(path))
                    .filter(file -> file.exists())
                    .collect(Collectors.toSet());
            final Set<PackageDetails> packages = dependencyFiles.parallelStream()
                    .flatMap(file -> extractPackagesFromFile(file, rootDir, filesForIScan, pkgMgr).stream())
                    .collect(Collectors.toSet());
            final List<Dependency> bdioComponents = packages.parallelStream()
                    .flatMap(pkg -> extractDependenciesFromPackage(pkg, pkgMgr).stream())
                    .collect(Collectors.toList());
            final DetectCodeLocation detectCodeLocation = codeLocationAssembler.generateCodeLocation(pkgMgr.getDefaultForge(), rootDir, bdioComponents);
            logSummary(bdioComponents, filesForIScan);
            return new Extraction.Builder().success(detectCodeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private List<Dependency> extractDependenciesFromPackage(final PackageDetails pkg, final LinuxPackageManager pkgMgr) {
        final List<Dependency> dependencies = new ArrayList<>();
        logger.debug(String.format("Package name//arch//version: %s//%s//%s", pkg.getPackageName(), pkg.getPackageArch(),
                pkg.getPackageVersion()));
        if (pkg.getPackageName() != null && pkg.getPackageVersion() != null && pkg.getPackageArch() != null) {
            dependencies.addAll(getDependencies(pkgMgr, pkg.getPackageName(), pkg.getPackageVersion(), pkg.getPackageArch()));
        }
        return dependencies;
    }

    private Set<PackageDetails> extractPackagesFromFile(final File f, final File sourceDir, final Set<File> filesForIScan, final LinuxPackageManager pkgMgr) {
        logger.trace(String.format("Querying package manager for %s", f.getAbsolutePath()));
        final DependencyFileDetails dependencyFileWithMetaData = new DependencyFileDetails(FileUtils.isUnder(sourceDir, f) ? true : false, f);
        final Set<PackageDetails> packages = new HashSet<>(pkgMgr.getPackages(executableRunner, filesForIScan, dependencyFileWithMetaData));
        logger.debug(String.format("Found %d packages for %s", packages.size(), f.getAbsolutePath()));
        return packages;
    }

    private Set<String> convertCompileCommandToDependencyFilePaths(final CompileCommand compileCommand, final File workingDir) {
        logger.info(String.format("Analyzing source file: %s", compileCommand.file));
        final Set<String> dependencyFilePaths = new HashSet<>();
        final Optional<File> depsMkFile = dependenciesListFileManager.generate(workingDir, compileCommand);
        dependencyFilePaths.addAll(dependenciesListFileManager.parse(depsMkFile.orElse(null)));
        depsMkFile.ifPresent(f -> f.delete());
        return dependencyFilePaths;
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
