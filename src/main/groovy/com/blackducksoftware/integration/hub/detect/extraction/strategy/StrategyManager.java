package com.blackducksoftware.integration.hub.detect.extraction.strategy;

public class StrategyManager {


    public <C> StrategyBuilder<C> newStrategy() {
        return new StrategyBuilder<>();
    }
}
