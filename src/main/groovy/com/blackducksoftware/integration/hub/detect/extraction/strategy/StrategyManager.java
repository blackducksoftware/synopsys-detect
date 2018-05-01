package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StrategyManager {

    @Autowired
    public List<StrategyProvider> strategyProviders;

    private final List<Strategy> allStrategies = new ArrayList<>();
    public List<Strategy> getAllStrategies() {
        return allStrategies;
    }
    public void init() {
        for (final StrategyProvider provider : strategyProviders) {
            allStrategies.addAll(provider.createStrategies());
        }
    }
}
