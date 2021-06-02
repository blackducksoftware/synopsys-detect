/*
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
import java.util.Set;

public class DetectorRuleSet {
    private final List<DetectorRule> orderedRules;
    private final Map<DetectorRule, Set<DetectorRule>> yieldsToRules;

    public DetectorRuleSet(List<DetectorRule> orderedRules, Map<DetectorRule, Set<DetectorRule>> yieldsToRules) {
        this.orderedRules = orderedRules;
        this.yieldsToRules = yieldsToRules;
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
}
