/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.packagist;

public class ComposerLockDetectableOptions {
    private final boolean includeDevDependencies;

    public ComposerLockDetectableOptions(final boolean includeDevDependencies) {
        this.includeDevDependencies = includeDevDependencies;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }
}
