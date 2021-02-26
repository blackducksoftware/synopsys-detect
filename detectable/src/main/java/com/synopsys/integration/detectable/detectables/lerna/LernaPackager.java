/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.lerna.lockfile.LernaLockFileResult;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.yarn.YarnPackager;
import com.synopsys.integration.detectable.detectables.yarn.YarnResult;
import com.synopsys.integration.util.NameVersion;

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

    public LernaResult generateLernaResult(File sourceDirectory, File rootPackageJson, List<LernaPackage> lernaPackages) {
        LernaLockFileResult rootLockFile = findLockFiles(sourceDirectory);
        LernaResult rootLernaResult = parse(sourceDirectory, rootPackageJson, rootLockFile, new ArrayList<>());

        List<NameVersion> externalPackages = lernaPackages.stream()
                                                 .map(lernaPackage -> new NameVersion(lernaPackage.getName(), lernaPackage.getVersion()))
                                                 .collect(Collectors.toList());

        List<CodeLocation> codeLocations = new ArrayList<>(rootLernaResult.getCodeLocations());
        for (LernaPackage lernaPackage : lernaPackages) {
            LernaResult lernaResult = extractPackage(lernaPackage, externalPackages, rootLockFile);
            if (lernaResult != null) {
                if (lernaResult.isSuccess()) {
                    lernaResult.getCodeLocations().stream()
                        .map(codeLocation -> new CodeLocation(codeLocation.getDependencyGraph(), codeLocation.getExternalId().orElse(null), new File(lernaPackage.getLocation())))
                        .forEach(codeLocations::add);
                } else if (lernaResult.isFailure()) {
                    String extractionErrorMessage = lernaResult.getException().map(Throwable::getMessage).orElse("Error message not found.");
                    logger.warn(String.format("Failed to extract lerna package: %s", extractionErrorMessage));
                    lernaResult.getException().ifPresent(exception -> logger.debug("Lerna Extraction Failure", exception));
                }
            }
        }

        return LernaResult.success(rootLernaResult.getProjectName(), rootLernaResult.getProjectVersionName(), codeLocations);
    }

    private @Nullable LernaResult extractPackage(LernaPackage lernaPackage, List<NameVersion> externalPackages, LernaLockFileResult rootLockFile) {
        String lernaPackageDetails = String.format("%s:%s at %s", lernaPackage.getName(), lernaPackage.getVersion(), lernaPackage.getLocation());

        if (!lernaOptions.shouldIncludePrivatePackages() && lernaPackage.isPrivate()) {
            logger.debug(String.format("Skipping extraction of private lerna package %s.", lernaPackageDetails));
            return null;
        }

        logger.debug(String.format("Now extracting Lerna package %s.", lernaPackageDetails));
        File lernaPackageDirectory = new File(lernaPackage.getLocation());
        LernaLockFileResult lockFile = findLockFiles(lernaPackageDirectory);
        File packagesPackageJson = fileFinder.findFile(lernaPackageDirectory, LernaDetectable.PACKAGE_JSON);

        if (packagesPackageJson == null) {
            return LernaResult.failure(new FileNotFoundException(String.format("A %s file was not found in %s.", LernaDetectable.PACKAGE_JSON, lernaPackageDirectory.getAbsolutePath())));
        }

        if (lockFile.hasLockFile()) {
            return parse(lernaPackageDirectory, packagesPackageJson, lockFile, externalPackages);
        } else {
            return parse(lernaPackageDirectory, packagesPackageJson, rootLockFile, externalPackages);
        }

    }

    private LernaLockFileResult findLockFiles(File searchDirectory) {
        try {
            File packageLockJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.PACKAGE_LOCK_JSON);
            if (packageLockJsonFile != null) {
                return LernaLockFileResult.foundNpm(FileUtils.readFileToString(packageLockJsonFile, StandardCharsets.UTF_8));
            }
            File shrinkwrapJsonFile = fileFinder.findFile(searchDirectory, LernaDetectable.SHRINKWRAP_JSON);
            if (shrinkwrapJsonFile != null) {
                return LernaLockFileResult.foundNpm(FileUtils.readFileToString(shrinkwrapJsonFile, StandardCharsets.UTF_8));
            }
            File yarnLockFile = fileFinder.findFile(searchDirectory, LernaDetectable.YARN_LOCK);
            if (yarnLockFile != null) {
                return LernaLockFileResult.foundYarn(FileUtils.readLines(yarnLockFile, StandardCharsets.UTF_8));
            }

            return LernaLockFileResult.foundNone();
        } catch (IOException exception) {
            return LernaLockFileResult.foundNone();
        }
    }

    private LernaResult parse(File directory, File packageJson, LernaLockFileResult lockFile, List<NameVersion> externalPackages) {
        String packageJsonContents;
        try {
            packageJsonContents = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return LernaResult.failure(e);
        }

        if (lockFile.getNpmLockContents().isPresent()) {
            //TODO: What if the NPM result is FAILED?
            NpmParseResult npmParseResult = npmLockfileParser
                                                .parse(packageJsonContents, lockFile.getNpmLockContents().get(), npmLockfileOptions.shouldIncludeDeveloperDependencies(), externalPackages);
            return LernaResult.success(npmParseResult.getProjectName(), npmParseResult.getProjectVersion(), Collections.singletonList(npmParseResult.getCodeLocation()));
        } else if (lockFile.getYarnLockContents().isPresent()) {
            YarnResult yarnResult = yarnPackager.generateYarnResult(packageJsonContents, lockFile.getYarnLockContents().get(), directory.getAbsolutePath(), externalPackages);

            if (yarnResult.getException().isPresent()) {
                return LernaResult.failure(yarnResult.getException().get());
            }

            return LernaResult.success(yarnResult.getProjectName(), yarnResult.getProjectVersionName(), Collections.singletonList(yarnResult.getCodeLocation()));
        } else {
            return LernaResult.failure(
                new FileNotFoundException(
                    String.format("Lerna extraction from %s requires one of the following files: %s, %s, %s",
                        directory.getAbsolutePath(),
                        LernaDetectable.PACKAGE_LOCK_JSON,
                        LernaDetectable.SHRINKWRAP_JSON,
                        LernaDetectable.YARN_LOCK
                    )
                )
            );
        }
    }
}
