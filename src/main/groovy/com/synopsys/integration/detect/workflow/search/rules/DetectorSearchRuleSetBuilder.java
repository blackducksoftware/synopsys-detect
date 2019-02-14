/**
 * detect-application
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
package com.synopsys.integration.detect.workflow.search.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;

public class DetectorSearchRuleSetBuilder {
    private final List<Detector> desiredDetectorOrder = new ArrayList<>();
    private final Map<Detector, DetectorSearchRuleBuilder> builderMap = new HashMap<>();
    private final List<DetectorYieldBuilder> yieldBuilders = new ArrayList<>();
    private final DetectorEnvironment environment;

    public DetectorSearchRuleSetBuilder(final DetectorEnvironment environment) {
        this.environment = environment;
    }

    public DetectorSearchRuleBuilder addBomTool(final Detector detector) {
        final DetectorSearchRuleBuilder builder = new DetectorSearchRuleBuilder(detector);
        desiredDetectorOrder.add(detector);
        builderMap.put(detector, builder);
        return builder;
    }

    public DetectorYieldBuilder yield(final Detector bomToolType) {
        final DetectorYieldBuilder builder = new DetectorYieldBuilder(bomToolType);
        yieldBuilders.add(builder);
        return builder;
    }

    public DetectorSearchRuleSet build() {
        final List<DetectorSearchRule> bomToolRules = new ArrayList<>();
        for (final Detector detector : desiredDetectorOrder) {
            final DetectorSearchRuleBuilder builder = builderMap.get(detector);
            for (final DetectorYieldBuilder yieldBuilder : yieldBuilders) {
                if (yieldBuilder.getYieldingDetector() == detector) {
                    builder.yield(yieldBuilder.getYieldingToDetector());
                }
            }
            bomToolRules.add(builder.build());
        }

        return new DetectorSearchRuleSet(bomToolRules, environment);
    }
}
