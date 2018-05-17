package com.blackducksoftware.integration.hub.detect.strategy.result;

public class MaxDepthExceededStrategyResult extends FailedStrategyResult {
    private final int depth;
    private final int maxDepth;

    public MaxDepthExceededStrategyResult(final int depth, final int maxDepth) {
        this.depth = depth;
        this.maxDepth = maxDepth;
    }

    @Override
    public String toDescription() {
        return "Max depth of " + maxDepth + " exceeded by " + depth;
    }
}
