package com.blackducksoftware.integration.hub.detect.bomtool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;

public class BomToolSearchRuleSetBuilder {
    private final List<BomTool> desiredBomToolOrder = new ArrayList<>();
    private final Map<BomTool, BomToolSearchRuleBuilder> builderMap = new HashMap<>();
    private final List<BomToolYieldBuilder> yieldBuilders = new ArrayList<>();
    private final BomToolEnvironment environment;

    public BomToolSearchRuleSetBuilder(final BomToolEnvironment environment) {
        this.environment = environment;
    }

    public BomToolSearchRuleBuilder addBomTool(final BomTool bomTool) {
        final BomToolSearchRuleBuilder builder = new BomToolSearchRuleBuilder(bomTool);
        desiredBomToolOrder.add(bomTool);
        builderMap.put(bomTool, builder);
        return builder;
    }

    public BomToolYieldBuilder yield(final BomToolType bomToolType) {
        final BomToolYieldBuilder builder = new BomToolYieldBuilder(bomToolType);
        yieldBuilders.add(builder);
        return builder;
    }

    public BomToolSearchRuleSet build() {
        final List<BomToolSearchRule> bomToolRules = new ArrayList<>();
        for (final BomTool bomTool : desiredBomToolOrder) {
            final BomToolSearchRuleBuilder builder = builderMap.get(bomTool);
            for (final BomToolYieldBuilder yieldBuilder : yieldBuilders) {
                if (yieldBuilder.getYieldingBomToolType() == bomTool.getBomToolType()) {
                    builder.yield(yieldBuilder.getYieldingToBomToolType());
                }
            }
            bomToolRules.add(builder.build());
        }

        return new BomToolSearchRuleSet(bomToolRules, environment);
    }
}
