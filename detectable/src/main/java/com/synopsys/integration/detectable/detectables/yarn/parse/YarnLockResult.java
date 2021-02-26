/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.Map;

import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class YarnLockResult {
    private final PackageJson rootPackageJson;
    private final Map<String, PackageJson> workspacePackageJsons;
    private final String yarnLockFilePath;
    private final YarnLock yarnLock;

    public YarnLockResult(PackageJson rootPackageJson, Map<String, PackageJson> workspacePackageJsons, String yarnLockFilePath, YarnLock yarnLock) {
        this.rootPackageJson = rootPackageJson;
        this.workspacePackageJsons = workspacePackageJsons;
        this.yarnLockFilePath = yarnLockFilePath;
        this.yarnLock = yarnLock;
    }

    public String getYarnLockFilePath() {
        return yarnLockFilePath;
    }

    public YarnLock getYarnLock() {
        return yarnLock;
    }

    public PackageJson getRootPackageJson() {
        return rootPackageJson;
    }

    public Map<String, PackageJson> getWorkspacePackageJsons() {
        return workspacePackageJsons;
    }
}
