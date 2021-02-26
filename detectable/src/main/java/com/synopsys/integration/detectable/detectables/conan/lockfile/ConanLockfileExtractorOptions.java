/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conan.lockfile;

import java.util.Optional;

public class ConanLockfileExtractorOptions {
    private final String lockfilePath;
    private final boolean includeDevDependencies;
    private final boolean preferLongFormExternalIds;

    public ConanLockfileExtractorOptions(String lockfilePath, boolean includeDevDependencies,
        boolean preferLongFormExternalIds) {
        this.lockfilePath = lockfilePath;
        this.includeDevDependencies = includeDevDependencies;
        this.preferLongFormExternalIds = preferLongFormExternalIds;
    }

    public Optional<String> getLockfilePath() {
        return Optional.ofNullable(lockfilePath);
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public boolean preferLongFormExternalIds() {
        return preferLongFormExternalIds;
    }
}
