package com.synopsys.integration.detectable.detectable.result;

public class MaxDepthExceededDetectableResult extends FailedDetectableResult {
    private final int depth;
    private final int maxDepth;

    public MaxDepthExceededDetectableResult(int depth, int maxDepth) {
        this.depth = depth;
        this.maxDepth = maxDepth;
    }

    @Override
    public String toDescription() {
        return "Max depth of " + maxDepth + " exceeded by " + depth;
    }
}
