/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.parse;

import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;

public class YarnLockResult {
    private final NullSafePackageJson rootPackageJson;
    private final YarnWorkspaces workspaceData;
    private final YarnLock yarnLock;

    public YarnLockResult(NullSafePackageJson rootPackageJson, YarnWorkspaces workspaceData, YarnLock yarnLock) {
        this.rootPackageJson = rootPackageJson;
        this.workspaceData = workspaceData;
        this.yarnLock = yarnLock;
    }

    public YarnLock getYarnLock() {
        return yarnLock;
    }

    public NullSafePackageJson getRootPackageJson() {
        return rootPackageJson;
    }

    public YarnWorkspaces getWorkspaceData() {
        return workspaceData;
    }
}
