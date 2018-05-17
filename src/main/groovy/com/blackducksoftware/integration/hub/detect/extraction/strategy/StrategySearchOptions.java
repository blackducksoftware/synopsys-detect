package com.blackducksoftware.integration.hub.detect.extraction.strategy;

public class StrategySearchOptions {
    private int maxDepth = 0;
    private boolean nestable = false;

    public StrategySearchOptions(final int maxDepth, final boolean nestable) {
        this.maxDepth = maxDepth;
        this.nestable = nestable;
    }

    public boolean getNestable() {
        return nestable;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
