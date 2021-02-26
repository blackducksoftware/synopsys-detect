/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.result;

public class NotSelfNestableDetectorResult extends FailedDetectorResult {
    public NotSelfNestableDetectorResult() {
        super("Nestable but this detector already applied in a parent directory.", NotSelfNestableDetectorResult.class);
    }
}
