package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.util.ArrayList;
import java.util.List;

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

}
