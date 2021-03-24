/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.rule;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DetectorRuleSet {
    private final List<DetectorRule> orderedRules;
    private final Map<DetectorRule, Set<DetectorRule>> yieldsToRules;
    private final Map<DetectorRule, DetectorRule> fallbackRules;

    public DetectorRuleSet(final List<DetectorRule> orderedRules, Map<DetectorRule, Set<DetectorRule>> yieldsToRules,
        final Map<DetectorRule, DetectorRule> fallbackRules) {
        this.orderedRules = orderedRules;
        this.yieldsToRules = yieldsToRules;
        this.fallbackRules = fallbackRules;
    }

    public List<DetectorRule> getOrderedDetectorRules() {
        return orderedRules;
    }

    public Set<DetectorRule> getYieldsTo(DetectorRule rule) {
        if (yieldsToRules.containsKey(rule)) {
            return yieldsToRules.get(rule);
        }
        return new HashSet<>();
    }

    public Optional<DetectorRule> getFallbackTo(DetectorRule rule) {
        if (fallbackRules.containsKey(rule)) {
            return Optional.of(fallbackRules.get(rule));
        }
        return Optional.empty();
    }

    public Optional<DetectorRule> getFallbackFrom(DetectorRule rule) {
        return fallbackRules.entrySet().stream()
                   .filter(it -> it.getValue().equals(rule))
                   .map(Map.Entry::getKey)
                   .findFirst();
    }
}
