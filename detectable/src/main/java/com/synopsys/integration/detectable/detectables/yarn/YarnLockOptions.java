/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn;

public class YarnLockOptions {
    private final boolean useProductionOnly;

    public YarnLockOptions(final boolean useProductionOnly) {
        this.useProductionOnly = useProductionOnly;
    }

    public boolean useProductionOnly() {
        return useProductionOnly;
    }
}
