package com.blackducksoftware.integration.hub.detect.manager.result.search;

public class StrategySearchYieldBuilder {

    private final StrategyType yieldingStrategyType;
    private StrategyType yieldingToStrategyType;

    public StrategySearchYieldBuilder(final StrategyType yieldingStrategyType) {
        this.yieldingStrategyType = yieldingStrategyType;
    }

    public StrategySearchYieldBuilder to(final StrategyType strategyType) {
        this.yieldingToStrategyType = strategyType;
        return this;
    }

    public StrategyType getYieldingStrategyType() {
        return yieldingStrategyType;
    }

    public StrategyType getYieldingToStrategyType() {
        return yieldingToStrategyType;
    }
}
