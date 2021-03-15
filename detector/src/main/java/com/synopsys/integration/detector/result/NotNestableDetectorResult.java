/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.result;

public class NotNestableDetectorResult extends FailedDetectorResult {
    public NotNestableDetectorResult() {
        super("Not nestable and a detector already applied in parent directory.", NotNestableDetectorResult.class);
    }
}
