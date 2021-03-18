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
import com.synopsys.integration.util.Stringable;

public class WorkspacePackageJson extends Stringable {
    private final File packageJsonFile;
    private final PackageJson packageJson;

    public WorkspacePackageJson(File packageJsonFile, PackageJson packageJson) {
        this.packageJsonFile = packageJsonFile;
        this.packageJson = packageJson;
    }

    public File getPackageJsonFile() {
        return packageJsonFile;
    }

    public PackageJson getPackageJson() {
        return packageJson;
    }
}
