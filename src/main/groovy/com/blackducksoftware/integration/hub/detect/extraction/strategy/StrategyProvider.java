package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;

public abstract class StrategyProvider {

    @SuppressWarnings("rawtypes")
    public abstract List<Strategy> createStrategies();

    public <C extends ExtractionContext, E extends Extractor<C>> StrategyBuilder<C, E> newStrategyBuilder(final Class<C> contextClass, final Class<E> extractorClass) {
        return new StrategyBuilder<>(contextClass, extractorClass);
    }
}
