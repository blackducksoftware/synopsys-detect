/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class ExecutableNotFoundDetectableResult extends FailedDetectableResult {
    private final String executableName;

    public ExecutableNotFoundDetectableResult(final String executableName) {
        this.executableName = executableName;
    }

    @Override
    public String toDescription() {
        return "No " + executableName + " executable was found.";
    }
}
