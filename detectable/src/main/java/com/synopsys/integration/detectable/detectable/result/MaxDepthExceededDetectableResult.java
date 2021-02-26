/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class MaxDepthExceededDetectableResult extends FailedDetectableResult {
    private final int depth;
    private final int maxDepth;

    public MaxDepthExceededDetectableResult(final int depth, final int maxDepth) {
        this.depth = depth;
        this.maxDepth = maxDepth;
    }

    @Override
    public String toDescription() {
        return "Max depth of " + maxDepth + " exceeded by " + depth;
    }
}
