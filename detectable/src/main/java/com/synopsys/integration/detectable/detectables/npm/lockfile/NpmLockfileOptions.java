/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.lockfile;

public class NpmLockfileOptions {
    final boolean includeDeveloperDependencies;
    final boolean includePeerDependencies;

    public NpmLockfileOptions(boolean includeDeveloperDependencies, boolean includePeerDependencies) {
        this.includeDeveloperDependencies = includeDeveloperDependencies;
        this.includePeerDependencies = includePeerDependencies;
    }

    public boolean shouldIncludeDeveloperDependencies() {
        return includeDeveloperDependencies;
    }

    public boolean shouldIncludePeerDependencies() {
        return includePeerDependencies;
    }
}
