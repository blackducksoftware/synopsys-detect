/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse;

import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class YarnLockResult {
    private final PackageJson packageJson;
    private final String yarnLockFilePath;
    private final YarnLock yarnLock;

    public YarnLockResult(PackageJson packageJson, String yarnLockFilePath, YarnLock yarnLock) {
        this.packageJson = packageJson;
        this.yarnLockFilePath = yarnLockFilePath;
        this.yarnLock = yarnLock;
    }

    public String getYarnLockFilePath() {
        return yarnLockFilePath;
    }

    public YarnLock getYarnLock() {
        return yarnLock;
    }

    public PackageJson getPackageJson() {
        return packageJson;
    }
}
