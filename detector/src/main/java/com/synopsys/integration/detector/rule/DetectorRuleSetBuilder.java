/**
 * detector
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.detector.base.DetectableCreatable;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectorRuleSetBuilder {

    private List<DetectorRule> rules = new ArrayList<>();
    private List<DetectorRuleYieldBuilder> yieldBuilders = new ArrayList<>();

    public DetectorRuleBuilder addDetector(DetectorType type, String name, DetectableCreatable detectableCreatable) {
        DetectorRuleBuilder ruleBuilder = new DetectorRuleBuilder(name, type, detectableCreatable);
        ruleBuilder.setDetectorRuleSetBuilder(this);
        return ruleBuilder;
    }

    public DetectorRuleSetBuilder add(final DetectorRule rule) {
        rules.add(rule);
        return this;
    }

    public DetectorRuleYieldBuilder yield(final DetectorRule rule) {
        final DetectorRuleYieldBuilder builder = new DetectorRuleYieldBuilder(rule);
        yieldBuilders.add(builder);
        return builder;
    }

    public DetectorRuleSet build() {
        Map<DetectorRule, Set<DetectorRule>> yieldsToRules = new HashMap<>();
        for (final DetectorRuleYieldBuilder yieldBuilder : yieldBuilders) {
            if (!yieldsToRules.containsKey(yieldBuilder.getYieldingDetector())){
                yieldsToRules.put(yieldBuilder.getYieldingDetector(), new HashSet<>());
            }
            yieldsToRules.get(yieldBuilder.getYieldingDetector()).add(yieldBuilder.getYieldingToDetector());
        }

        return new DetectorRuleSet(rules, yieldsToRules);//TODO: Confirm I don't need to recalcute detector order here.
    }
}
