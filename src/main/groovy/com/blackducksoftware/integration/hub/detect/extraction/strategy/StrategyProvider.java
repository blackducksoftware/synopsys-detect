package com.blackducksoftware.integration.hub.detect.extraction.strategy;

public class StrategyProvider {



    public <C, E> StrategyBuilder<C, E> newStrategy(final Class<C> contextClass, final Class<E> extractorClass) {
        return new StrategyBuilder<>();
    }
}
