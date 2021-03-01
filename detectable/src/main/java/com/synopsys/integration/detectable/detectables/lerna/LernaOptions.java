/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.lerna;

public class LernaOptions {
    private final boolean includePrivatePackages;

    public LernaOptions(boolean includePrivatePackages) {
        this.includePrivatePackages = includePrivatePackages;
    }

    public boolean shouldIncludePrivatePackages() {
        return includePrivatePackages;
    }
}
