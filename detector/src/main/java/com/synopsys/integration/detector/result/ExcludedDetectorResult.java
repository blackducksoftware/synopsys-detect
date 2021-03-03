/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.result;

public class ExcludedDetectorResult extends FailedDetectorResult {
    public ExcludedDetectorResult() {
        super("Detector type was excluded.", ExcludedDetectorResult.class);
    }
}
