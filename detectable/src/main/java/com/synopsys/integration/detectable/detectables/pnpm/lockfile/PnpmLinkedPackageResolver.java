/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;

public class PnpmLinkedPackageResolver {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final File projectRoot;
    private PackageJsonFiles packageJsonFiles;

    public PnpmLinkedPackageResolver(File projectRoot, PackageJsonFiles packageJsonFiles) {
        this.projectRoot = projectRoot;
        this.packageJsonFiles = packageJsonFiles;
    }

    @Nullable
    public String resolveVersionOfLinkedPackage(@Nullable String reportingProjectPackagePath, String relativePathToLinkedPackage) {
        File reportingPackage;
        if (reportingProjectPackagePath != null) {
            reportingPackage = new File(projectRoot, reportingProjectPackagePath);
        } else {
            reportingPackage = projectRoot;
        }
        File linkedPackage = new File(reportingPackage, relativePathToLinkedPackage);
        File packageJsonFile = new File(linkedPackage, "package.json");

        if (!packageJsonFile.exists()) {
            logger.debug(String.format("Unable to resolve version for linked package: %s", linkedPackage));
            return null;
        }

        try {
            NullSafePackageJson packageJson = packageJsonFiles.read(packageJsonFile);
            return packageJson.getVersionString();
        } catch (IOException e) {
            logger.debug(String.format("Unable to parse package.json: %s", packageJsonFile.getAbsolutePath()));
            return null;
        }
    }

}
