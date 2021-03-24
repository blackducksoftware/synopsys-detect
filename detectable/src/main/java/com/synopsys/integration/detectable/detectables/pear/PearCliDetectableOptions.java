/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pear;

public class PearCliDetectableOptions {
    private final boolean onlyGatherRequired;

    public PearCliDetectableOptions(final boolean onlyGatherRequired) {
        this.onlyGatherRequired = onlyGatherRequired;
    }

    public boolean onlyGatherRequired() {
        return onlyGatherRequired;
    }
}
