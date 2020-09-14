/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.yarn.YarnPackager;
import com.synopsys.integration.detectable.detectables.yarn.YarnResult;

public class LernaPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final NpmLockfilePackager npmLockfileParser;
    private final NpmLockfileOptions npmLockfileOptions;
    private final YarnPackager yarnPackager;
    private final LernaOptions lernaOptions;

    public LernaPackager(FileFinder fileFinder, NpmLockfilePackager npmLockfileParser, NpmLockfileOptions npmLockfileOptions, YarnPackager yarnPackager, LernaOptions lernaOptions) {
        this.fileFinder = fileFinder;
        this.npmLockfileParser = npmLockfileParser;
        this.npmLockfileOptions = npmLockfileOptions;
        this.yarnPackager = yarnPackager;
        this.lernaOptions = lernaOptions;
    }

    public LernaResult generateLernaResult(File sourceDirectory, List<LernaPackage> lernaPackages) {
        LernaMissingDependencyHandler lernaMissingDependencyHandler = new LernaMissingDependencyHandler(lernaPackages);

        LernaResult rootLernaResult = extractWithRootLockfile(sourceDirectory, sourceDirectory, lernaMissingDependencyHandler);
        if (rootLernaResult.isFailure()) {
            return rootLernaResult;
        }

        List<CodeLocation> codeLocations = new ArrayList<>(rootLernaResult.getCodeLocations());
        for (LernaPackage lernaPackage : lernaPackages) {
            String lernaPackageDetails = String.format("%s:%s at %s", lernaPackage.getName(), lernaPackage.getVersion(), lernaPackage.getLocation());

            if (!lernaOptions.shouldIncludePrivatePackages() && lernaPackage.isPrivate()) {
                logger.debug(String.format("Skipping extraction of private lerna package %s.", lernaPackageDetails));
                continue;
            }

            logger.debug(String.format("Now extracting Lerna package %s.", lernaPackageDetails));
            File lernaPackageDirectory = new File(lernaPackage.getLocation());

            LernaResult lernaResult = extractLernaPackage(sourceDirectory, lernaPackageDirectory, lernaMissingDependencyHandler);
            if (lernaResult.isSuccess()) {
                logger.debug(String.format("Extraction completed successfully on %s.", lernaPackageDetails));
                lernaResult.getCodeLocations().stream()
                    .map(codeLocation -> new CodeLocation(codeLocation.getDependencyGraph(), codeLocation.getExternalId().orElse(null), lernaPackageDirectory))
                    .forEach(codeLocations::add);
            } else {
                String extractionErrorMessage = lernaResult.getException().map(Throwable::getMessage).orElse("Error message not found.");
                logger.warn(String.format("Failed to extract lerna package: %s", extractionErrorMessage));
                lernaResult.getException().ifPresent(exception -> logger.debug("Lerna Extraction Failure", exception));
            }
        }

        return LernaResult.success(rootLernaResult.getProjectName(), rootLernaResult.getProjectVersionName(), codeLocations);
    }

    private LernaResult extractLernaPackage(File sourceDirectory, File lernaPackageDirectory, LernaMissingDependencyHandler lernaMissingDependencyHandler) {
        LernaResult lernaResult = extractWithLocalLockfile(lernaPackageDirectory, lernaMissingDependencyHandler);
        if (lernaResult.getException().isPresent()) {
            lernaResult = extractWithRootLockfile(lernaPackageDirectory, sourceDirectory, lernaMissingDependencyHandler);
        }

        return lernaResult;
    }

    private LernaResult extractWithRootLockfile(File lernaPackageDirectory, File sourceDirectory, LernaMissingDependencyHandler lernaMissingDependencyHandler) {
        File packageJsonFile = fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON);
        if (packageJsonFile == null) {
            return LernaResult.failure(new FileNotFoundException(String.format("A %s file was not found in %s.", LernaDetectable.PACKAGE_JSON, lernaPackageDirectory.getAbsolutePath())));
        }
        return extractWithAnyLockfile(sourceDirectory, packageJsonFile, lernaMissingDependencyHandler);
    }

    private LernaResult extractWithLocalLockfile(File lernaPackageDirectory, LernaMissingDependencyHandler lernaMissingDependencyHandler) {
        File packageJsonFile = fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON);
        if (packageJsonFile == null) {
            return LernaResult.failure(new FileNotFoundException(String.format("A %s file was not found in %s.", LernaDetectable.PACKAGE_JSON, lernaPackageDirectory.getAbsolutePath())));
        }
        return extractWithAnyLockfile(lernaPackageDirectory, packageJsonFile, lernaMissingDependencyHandler);
    }

    private LernaResult extractWithAnyLockfile(File searchDirectory, File packageJsonFile, LernaMissingDependencyHandler lernaMissingDependencyHandler) {
        File packageLockJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.PACKAGE_LOCK_JSON);
        File shrinkwrapJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.SHRINKWRAP_JSON);
        File yarnLockFile = fileFinder.findFile(searchDirectory, LernaDetectable.YARN_LOCK);

        if (packageLockJsonFile != null) {
            return extractFromNpmLockfile(packageJsonFile, packageLockJsonFile, lernaMissingDependencyHandler);
        } else if (shrinkwrapJsonFile != null) {
            return extractFromNpmLockfile(packageJsonFile, shrinkwrapJsonFile, lernaMissingDependencyHandler);
        } else if (yarnLockFile != null) {
            return extractFromYarnLock(packageJsonFile, yarnLockFile, lernaMissingDependencyHandler);
        } else {
            return LernaResult.failure(
                new FileNotFoundException(
                    String.format("Lerna extraction from %s requires one of the following files: %s, %s, %s",
                        searchDirectory.getAbsolutePath(),
                        LernaDetectable.PACKAGE_LOCK_JSON,
                        LernaDetectable.SHRINKWRAP_JSON,
                        LernaDetectable.YARN_LOCK
                    )
                )
            );
        }
    }

    private LernaResult extractFromNpmLockfile(File packageJsonFile, File npmLockfile, LernaMissingDependencyHandler lernaMissingDependencyHandler) {
        try {
            String packageJsonText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
            String lockfileText = FileUtils.readFileToString(npmLockfile, StandardCharsets.UTF_8);

            NpmParseResult npmParseResult = npmLockfileParser.parse(packageJsonText, lockfileText, npmLockfileOptions.shouldIncludeDeveloperDependencies(), lernaMissingDependencyHandler::handleMissingNpmDependency);

            return LernaResult.success(npmParseResult.getProjectName(), npmParseResult.getProjectVersion(), Collections.singletonList(npmParseResult.getCodeLocation()));
        } catch (IOException exception) {
            return LernaResult.failure(exception);
        }
    }

    private LernaResult extractFromYarnLock(File packageJsonFile, File yarnLockFile, LernaMissingDependencyHandler lernaMissingDependencyHandler) {
        try {
            String packageJsonText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
            List<String> yarnLockLines = FileUtils.readLines(yarnLockFile, StandardCharsets.UTF_8);

            YarnResult yarnResult = yarnPackager.generateYarnResult(packageJsonText, yarnLockLines, yarnLockFile.getAbsolutePath(), lernaMissingDependencyHandler::handleMissingYarnDependency);

            if (yarnResult.getException().isPresent()) {
                throw yarnResult.getException().get();
            }

            return LernaResult.success(yarnResult.getProjectName(), yarnResult.getProjectVersionName(), Collections.singletonList(yarnResult.getCodeLocation()));
        } catch (Exception exception) {
            return LernaResult.failure(exception);
        }
    }
}
