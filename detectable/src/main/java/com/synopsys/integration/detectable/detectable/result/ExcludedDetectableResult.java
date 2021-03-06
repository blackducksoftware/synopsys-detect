/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class ExcludedDetectableResult extends FailedDetectableResult {
    @Override
    public String toDescription() {
        return "Detector type was excluded.";
    }
}
