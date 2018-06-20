package com.blackducksoftware.integration.hub.detect.manager.result.search;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.strategy.Strategy;

public class StrategySearchRules {
    private final Strategy strategy;
    private final int maxDepth;
    private final boolean nestable;
    private final List<StrategyType> yieldsTo;

    public StrategySearchRules(final Strategy strategy, final int maxDepth, final boolean nestable, final List<StrategyType> yieldsTo) {
        this.strategy = strategy;
        this.maxDepth = maxDepth;
        this.nestable = nestable;
        this.yieldsTo = yieldsTo;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public boolean isNestable() {
        return nestable;
    }

    public List<StrategyType> getYieldsTo() {
        return yieldsTo;
    }
}
