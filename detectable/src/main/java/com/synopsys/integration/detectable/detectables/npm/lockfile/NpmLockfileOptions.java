/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.lockfile;

public class NpmLockfileOptions {
    final boolean includeDeveloperDependencies;

    public NpmLockfileOptions(final boolean includeDeveloperDependencies) {
        this.includeDeveloperDependencies = includeDeveloperDependencies;
    }

    public boolean shouldIncludeDeveloperDependencies() {
        return includeDeveloperDependencies;
    }
}
