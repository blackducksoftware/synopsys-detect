package com.blackducksoftware.integration.hub.detect.extraction.strategy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StrategyManager {

    @Autowired
    public List<StrategyCoordinator> strategyCoordinators;

    @Autowired
    public List<Strategy> strategies;

    public List<Strategy> getAllStrategies() {
        return strategies;
    }

    public void init() {
        for (final StrategyCoordinator coordinator : strategyCoordinators) {
            coordinator.init();
        }
    }
}
