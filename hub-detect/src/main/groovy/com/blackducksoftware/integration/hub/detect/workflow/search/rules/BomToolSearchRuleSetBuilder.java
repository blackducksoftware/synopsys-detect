/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.workflow.search.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;

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
