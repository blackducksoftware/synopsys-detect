/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse;

import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;

public class YarnLockResult {
    private final PackageJson rootPackageJson;
    private final YarnWorkspaces workspaceData;
    private final String yarnLockFilePath;
    private final YarnLock yarnLock;

    public YarnLockResult(PackageJson rootPackageJson, YarnWorkspaces workspaceData, String yarnLockFilePath, YarnLock yarnLock) {
        this.rootPackageJson = rootPackageJson;
        this.workspaceData = workspaceData;
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

    public YarnWorkspaces getWorkspaceData() {
        return workspaceData;
    }
}
