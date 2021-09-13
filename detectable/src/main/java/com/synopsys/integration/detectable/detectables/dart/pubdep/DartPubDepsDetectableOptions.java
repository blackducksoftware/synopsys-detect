/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.dart.pubdep;

public class DartPubDepsDetectableOptions {
    private boolean excludeDevDependencies;

    public DartPubDepsDetectableOptions(boolean excludeDevDependencies) {
        this.excludeDevDependencies = excludeDevDependencies;
    }

    public boolean isExcludeDevDependencies() {
        return excludeDevDependencies;
    }
}
