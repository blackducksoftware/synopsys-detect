package com.blackducksoftware.integration.hub.detect.manager.result.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.StrategySearchOptions;

public class StrategySet {

    private final List<Strategy> orderedStrategies = new ArrayList<>();
    private final Map<StrategyType, List<StrategyType>> yieldMap = new HashMap<>();


    public void addStrategy(final Strategy strategy, final StrategySearchOptions searchOptions) {

    }

    public void yield(final StrategyType strategyYielding, final StrategyType strategyWinner) {
        if (!yieldMap.containsKey(strategyYielding)) {
            yieldMap.put(strategyYielding, new ArrayList<>());
        }
        yieldMap.get(strategyYielding).add(strategyWinner);
    }
}
