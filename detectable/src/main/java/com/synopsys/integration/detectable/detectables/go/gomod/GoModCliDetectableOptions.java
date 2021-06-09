/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod;

public class GoModCliDetectableOptions {
    private final boolean dependencyVerificationEnabled;

    public GoModCliDetectableOptions(boolean dependencyVerificationEnabled) {
        this.dependencyVerificationEnabled = dependencyVerificationEnabled;
    }

    public boolean isDependencyVerificationEnabled() {
        return dependencyVerificationEnabled;
    }
}
