/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
        if (yieldsToRules.containsKey(rule))
            return yieldsToRules.get(rule);
        return new HashSet<>();
    }

    public Optional<DetectorRule> getFallbackTo(DetectorRule rule) {
        if (fallbackRules.containsKey(rule))
            return Optional.of(fallbackRules.get(rule));
        return Optional.empty();
    }

    public Optional<DetectorRule> getFallbackFrom(DetectorRule rule) {
        return fallbackRules.entrySet().stream()
                   .filter(it -> it.getValue().equals(rule))
                   .map(Map.Entry::getKey)
                   .findFirst();
    }
}
