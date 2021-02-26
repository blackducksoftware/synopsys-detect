/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

import com.synopsys.integration.util.OperatingSystemType;

public class WrongOperatingSystemResult extends FailedDetectableResult {
    private final OperatingSystemType currentOperatingSystem;

    public WrongOperatingSystemResult(final OperatingSystemType currentOperatingSystem) {
        this.currentOperatingSystem = currentOperatingSystem;
    }

    @Override
    public String toDescription() {
        return String.format("Cannot run on %s", currentOperatingSystem.toString());
    }

}
