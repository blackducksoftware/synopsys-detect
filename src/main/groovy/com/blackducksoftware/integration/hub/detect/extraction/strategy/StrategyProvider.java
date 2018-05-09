package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;

public abstract class StrategyProvider {

    protected List<Strategy> allStrategies = new ArrayList<>();

    public abstract void init();
    public void lateInit() {}

    public List<Strategy> getAllStrategies(){
        return allStrategies;
    }

    protected void add(final Strategy ... strategy) {
        for (final Strategy aStrategy : strategy) {
            allStrategies.add(aStrategy);
        }
    }

    public <C extends ExtractionContext, E extends Extractor<C>> StrategyBuilder<C, E> newStrategyBuilder(final Class<C> contextClass, final Class<E> extractorClass) {
        return new StrategyBuilder<>(contextClass, extractorClass);
    }

}
