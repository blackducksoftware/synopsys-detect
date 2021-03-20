/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.io.File;

import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class WorkspacePackageJson {
    private final File packageJsonFile;
    private final PackageJson packageJson;
    private final String dirRelativePath;

    public WorkspacePackageJson(File packageJsonFile, PackageJson packageJson, String dirRelativePath) {
        this.packageJsonFile = packageJsonFile;
        this.packageJson = packageJson;
        this.dirRelativePath = dirRelativePath;
    }

    public File getPackageJsonFile() {
        return packageJsonFile;
    }

    public PackageJson getPackageJson() {
        return packageJson;
    }
    
    public String getDirRelativePath() {
        return dirRelativePath;
    }
}
