/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

import java.util.List;

public class ExecutablesNotFoundDetectableResult extends FailedDetectableResult {
    private final List<String> executableNames;

    public ExecutablesNotFoundDetectableResult(List<String> executableNames) {
        this.executableNames = executableNames;
    }

    @Override
    public String toDescription() {
        return "None of the following executables were found " + String.join(",", executableNames);
    }
}
