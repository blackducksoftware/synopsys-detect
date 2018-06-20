package com.blackducksoftware.integration.hub.detect.manager.result.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;

public class StrategySearchRulesSetBuilder {
    private final List<Strategy> desiredStrategyOrder = new ArrayList<>();
    private final Map<Strategy, StrategySearchRulesBuilder> builderMap = new HashMap<>();
    private final List<StrategySearchYieldBuilder> yieldBuilders = new ArrayList<>();
    private final StrategyEnvironment environment;

    public StrategySearchRulesSetBuilder(final StrategyEnvironment environment) {
        this.environment = environment;
    }

    public StrategySearchRulesBuilder addStrategy(final Strategy strategy) {
        final StrategySearchRulesBuilder builder = new StrategySearchRulesBuilder(strategy);
        desiredStrategyOrder.add(strategy);
        builderMap.put(strategy, builder);
        return builder;
    }

    public StrategySearchYieldBuilder yield(final StrategyType strategyType) {
        final StrategySearchYieldBuilder builder = new StrategySearchYieldBuilder(strategyType);
        yieldBuilders.add(builder);
        return builder;
    }

    public StrategySearchRuleSet build() {
        final List<StrategySearchRules> strategyRules = new ArrayList<>();
        for (final Strategy strategy : desiredStrategyOrder) {
            final StrategySearchRulesBuilder builder = builderMap.get(strategy);
            for (final StrategySearchYieldBuilder yieldBuilder : yieldBuilders) {
                if (yieldBuilder.getYieldingStrategyType() == strategy.getStrategyType()) {
                    builder.yield(yieldBuilder.getYieldingToStrategyType());
                }
            }
            strategyRules.add(builder.build());
        }

        return new StrategySearchRuleSet(strategyRules, environment);
    }
}
