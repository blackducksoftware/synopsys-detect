package com.blackducksoftware.integration.hub.detect.extraction.strategy;

public class StrategyContextBuilder<C> {


    public <E> StrategyContextExtractorBuilder<C, E> withExtractor(final Class<E> e) {
        return new StrategyContextExtractorBuilder<>();
    }

}
