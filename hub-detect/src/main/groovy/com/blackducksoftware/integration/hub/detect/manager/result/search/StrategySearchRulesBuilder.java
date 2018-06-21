package com.blackducksoftware.integration.hub.detect.manager.result.search;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.strategy.Strategy;

public class StrategySearchRulesBuilder {
    private final Strategy strategy;
    private int maxDepth;
    private boolean nestable;
    private final List<StrategyType> yieldsTo;

    public StrategySearchRulesBuilder(final Strategy strategy) {
        this.strategy = strategy;
        yieldsTo = new ArrayList<>();
    }

    public StrategySearchRulesBuilder defaultNotNested() {
        maxDepth(Integer.MAX_VALUE);
        return nestable(false);
    }

    public StrategySearchRulesBuilder defaultNested() {
        maxDepth(Integer.MAX_VALUE);
        return nestable(true);
    }

    public StrategySearchRulesBuilder maxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public StrategySearchRulesBuilder nestable(final boolean nestable) {
        this.nestable = nestable;
        return this;
    }

    public StrategySearchRulesBuilder yield(final StrategyType type) {
        this.yieldsTo.add(type);
        return this;
    }

    public StrategySearchRules build() {
        return new StrategySearchRules(strategy, maxDepth, nestable, yieldsTo);
    }
}
