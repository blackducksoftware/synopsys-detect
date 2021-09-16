/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detector.base.DetectableCreatable;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectorRuleSetBuilder {
    private final List<DetectorRule> rules = new ArrayList<>();
    private final List<DetectorRuleYieldBuilder> yieldBuilders = new ArrayList<>();

    public <T extends Detectable> DetectorRuleBuilder addDetector(DetectorType type, String name, Class<T> detectableClass, DetectableCreatable<T> detectableCreatable) {
        DetectorRuleBuilder ruleBuilder = new DetectorRuleBuilder<>(name, type, detectableClass, detectableCreatable);
        ruleBuilder.setDetectorRuleSetBuilder(this);
        return ruleBuilder;
    }

    public DetectorRuleSetBuilder add(DetectorRule rule) {
        rules.add(rule);
        return this;
    }

    public DetectorRuleYieldBuilder yield(DetectorRule rule) {
        DetectorRuleYieldBuilder builder = new DetectorRuleYieldBuilder(rule);
        yieldBuilders.add(builder);
        return builder;
    }

    public DetectorRuleSet build() {
        Map<DetectorRule, Set<DetectorRule>> yieldsToRules = buildYield();

        List<DetectorRule> orderedRules = new ArrayList<>();
        boolean atLeastOneRuleAdded = true;

        while (orderedRules.size() < rules.size() && atLeastOneRuleAdded) {
            List<DetectorRule> satisfiedRules = rules.stream()
                                                    .filter(rule -> !orderedRules.contains(rule))
                                                    .filter(rule -> yieldSatisfied(rule, orderedRules, yieldsToRules))
                                                    .collect(Collectors.toList());

            atLeastOneRuleAdded = satisfiedRules.size() > 0;
            orderedRules.addAll(satisfiedRules);
        }

        if (orderedRules.size() != rules.size()) {
            throw new RuntimeException("Unable to order detector rules.");
        }

        return new DetectorRuleSet(orderedRules, yieldsToRules);
    }

    private Map<DetectorRule, Set<DetectorRule>> buildYield() {
        Map<DetectorRule, Set<DetectorRule>> yieldsToRules = new HashMap<>();
        for (DetectorRuleYieldBuilder yieldBuilder : yieldBuilders) {
            if (!yieldsToRules.containsKey(yieldBuilder.getYieldingDetector())) {
                yieldsToRules.put(yieldBuilder.getYieldingDetector(), new HashSet<>());
            }
            yieldsToRules.get(yieldBuilder.getYieldingDetector()).add(yieldBuilder.getYieldingToDetector());
        }
        return yieldsToRules;
    }

    //returns true when all all detectors the rule yields to have already been added.
    private boolean yieldSatisfied(DetectorRule rule, List<DetectorRule> orderedRules, Map<DetectorRule, Set<DetectorRule>> yieldsToRules) {
        if (yieldsToRules.containsKey(rule)) {
            boolean yieldedSatisfied = true;
            for (DetectorRule yield : yieldsToRules.get(rule)) {
                if (!orderedRules.contains(yield)) {
                    yieldedSatisfied = false;
                }
            }
            return yieldedSatisfied;
        } else {
            if (!yieldsToRules.containsKey(rule)) {
                return true;
            }
        }

        // if ordered rules is missing ANY of the yielded rules, return false
        // otherwise return true
        return orderedRules.containsAll(yieldsToRules.get(rule));
    }

}
