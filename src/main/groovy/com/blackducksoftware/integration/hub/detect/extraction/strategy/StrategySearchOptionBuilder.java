package com.blackducksoftware.integration.hub.detect.extraction.strategy;

public class StrategySearchOptionBuilder {
    private int maxDepth = 0;
    private boolean nestable = false;

    public StrategySearchOptionBuilder() {

    }

    public StrategySearchOptionBuilder nestable() {
        nestable = true;
        maxDepth = Integer.MAX_VALUE;
        return this;
    }

    public StrategySearchOptionBuilder maxDepth(final int depth) {
        maxDepth = depth;
        return this;
    }

    public StrategySearchOptions build() {
        return new StrategySearchOptions(maxDepth, nestable);
    }
}
