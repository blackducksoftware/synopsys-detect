/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.result;

public class MaxDepthExceededDetectorResult extends FailedDetectorResult {
    public MaxDepthExceededDetectorResult(final int depth, final int maxDepth) {
        super(String.format("Max depth of %d exceeded by %d", maxDepth, depth), MaxDepthExceededDetectorResult.class);
    }
}
