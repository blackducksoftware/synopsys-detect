/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

public class PnpmLockDetectableOptions {
    private boolean includeDevDependencies;
    private boolean includeOptionalDependencies;

    public PnpmLockDetectableOptions(boolean includeDevDependencies, boolean includeOptionalDependencies) {
        this.includeDevDependencies = includeDevDependencies;
        this.includeOptionalDependencies = includeOptionalDependencies;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public boolean shouldIncludeOptionalDependencies() {
        return includeOptionalDependencies;
    }
}
