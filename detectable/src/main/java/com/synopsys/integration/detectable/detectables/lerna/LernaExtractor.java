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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileExtractor;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockExtractor;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;

public class LernaExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final LernaPackageDiscoverer lernaPackageDiscoverer;
    private final NpmLockfileExtractor npmLockfileExtractor;
    private final NpmLockfileOptions npmLockfileOptions;
    private final YarnLockExtractor yarnLockExtractor;
    private final YarnLockOptions yarnLockOptions;

    public LernaExtractor(final FileFinder fileFinder, final LernaPackageDiscoverer lernaPackageDiscoverer, final NpmLockfileExtractor npmLockfileExtractor,
        final NpmLockfileOptions npmLockfileOptions,
        final YarnLockExtractor yarnLockExtractor, final YarnLockOptions yarnLockOptions) {
        this.fileFinder = fileFinder;
        this.lernaPackageDiscoverer = lernaPackageDiscoverer;
        this.npmLockfileExtractor = npmLockfileExtractor;
        this.npmLockfileOptions = npmLockfileOptions;
        this.yarnLockExtractor = yarnLockExtractor;
        this.yarnLockOptions = yarnLockOptions;
    }

    public Extraction extract(final DetectableEnvironment detectableEnvironment, final File lernaExecutable) {
        final File sourceDirectory = detectableEnvironment.getDirectory();

        try {
            final List<LernaPackage> lernaPackages = lernaPackageDiscoverer.discoverLernaPackages(sourceDirectory, lernaExecutable);

            final List<CodeLocation> codeLocations = new ArrayList<>();
            for (final LernaPackage lernaPackage : lernaPackages) {
                logger.debug(String.format("Now extracting Lerna package %s:%s at %s.", lernaPackage.getName(), lernaPackage.getVersion(), lernaPackage.getLocation()));

                final Extraction extraction = extractLernaPackage(sourceDirectory, lernaPackage);
                if (extraction.isSuccess()) {
                    logger.debug(String.format("Extraction completed successfully on %s:%s.", lernaPackage.getName(), lernaPackage.getLocation()));
                    codeLocations.addAll(extraction.getCodeLocations());
                } else {
                    logger.warn(String.format("Failed to extract lerna package: %s", extraction.getError().getMessage()));
                    logger.debug("Lerna Extraction Failure", extraction.getError());
                }
            }

            return new Extraction.Builder().success(codeLocations).build(); // TODO: Add project name/version info.
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Extraction extractLernaPackage(final File sourceDirectory, final LernaPackage lernaPackage) {
        final File lernaPackageDirectory = new File(sourceDirectory.getParent(), lernaPackage.getLocation());

        Extraction extraction = extractWithLocalLockfile(lernaPackageDirectory);
        if (!extraction.isSuccess()) {
            extraction = extractWithRootLockfile(lernaPackageDirectory, sourceDirectory);
        }

        return extraction;
    }

    private Extraction extractWithRootLockfile(final File lernaPackageDirectory, final File sourceDirectory) {
        final File packageJsonFile = fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON);
        try {
            return extractWithAnyLockfile(sourceDirectory, packageJsonFile);
        } catch (final FileNotFoundException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Extraction extractWithLocalLockfile(final File lernaPackageDirectory) {
        final File packageJsonFile = fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON);
        try {
            return extractWithAnyLockfile(lernaPackageDirectory, packageJsonFile);
        } catch (final FileNotFoundException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Extraction extractWithAnyLockfile(final File searchDirectory, final File packageJsonFile) throws FileNotFoundException {
        final File packageLockJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.PACKAGE_LOCK_JSON);
        final File shrinkwrapJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.SHRINKWRAP_JSON);
        final File yarnLockFile = fileFinder.findFile(searchDirectory, LernaDetectable.YARN_LOCK);

        if (packageLockJsonFile != null) {
            return extractFromPackageLock(packageJsonFile, packageLockJsonFile);
        } else if (shrinkwrapJsonFile != null) {
            return extractFromShrinkwrap(packageJsonFile, shrinkwrapJsonFile);
        } else if (yarnLockFile != null) {
            return extractFromYarnLock(packageJsonFile, yarnLockFile);
        } else {
            throw new FileNotFoundException(
                String.format("Lerna extraction from %s requires one of the following files: %s, %s, %s",
                    searchDirectory.getAbsolutePath(),
                    LernaDetectable.PACKAGE_LOCK_JSON,
                    LernaDetectable.SHRINKWRAP_JSON,
                    LernaDetectable.YARN_LOCK)
            );
        }
    }

    private Extraction extractFromShrinkwrap(final File packageJsonFile, final File shrinkwrapJsonFile) {
        return npmLockfileExtractor.extract(shrinkwrapJsonFile, packageJsonFile, npmLockfileOptions.shouldIncludeDeveloperDependencies());
    }

    private Extraction extractFromPackageLock(final File packageJsonFile, final File packageLockJsonFile) {
        return npmLockfileExtractor.extract(packageLockJsonFile, packageJsonFile, npmLockfileOptions.shouldIncludeDeveloperDependencies());
    }

    private Extraction extractFromYarnLock(final File packageJsonFile, final File yarnLockFile) {
        return yarnLockExtractor.extract(yarnLockFile, packageJsonFile, yarnLockOptions);
    }
}
