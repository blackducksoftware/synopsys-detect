package com.blackducksoftware.integration.hub.detect.extraction.strategy;

public class StrategyContextExtractorBuilder<C, E> {

    public Strategy<C, E> build() {
        return new Strategy<>();
    }
}
