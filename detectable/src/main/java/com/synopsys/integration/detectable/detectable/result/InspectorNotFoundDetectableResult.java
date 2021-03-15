/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class InspectorNotFoundDetectableResult extends FailedDetectableResult {
    private final String inspectorName;

    public InspectorNotFoundDetectableResult(final String inspectorName) {
        this.inspectorName = inspectorName;
    }

    @Override
    public String toDescription() {
        return "No " + inspectorName + " inspector was found.";
    }
}
