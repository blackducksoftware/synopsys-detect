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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolExcludedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.ForcedNestedPassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.MaxDepthExceededBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.NotNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.NotSelfNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.YieldedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling.BomToolProfiler;

public class BomToolSearchRuleSet {

    private final List<BomToolSearchRule> orderedBomToolRules;
    private final BomToolEnvironment environment;
    private final BomToolProfiler bomToolProfiler;

    public BomToolSearchRuleSet(final List<BomToolSearchRule> orderedBomToolRules, final BomToolEnvironment environment, final BomToolProfiler bomToolProfiler) {
        this.orderedBomToolRules = orderedBomToolRules;
        this.environment = environment;
        this.bomToolProfiler = bomToolProfiler;
    }

    public List<BomToolEvaluation> evaluate() {
        final List<BomToolEvaluation> evaluations = new ArrayList<>();
        final List<BomTool> appliedSoFar = new ArrayList<>();
        for (final BomToolSearchRule searchRule : orderedBomToolRules) {
            final BomTool bomTool = searchRule.getBomTool();
            final BomToolEvaluation evaluation = new BomToolEvaluation(bomTool, environment);
            evaluations.add(evaluation);
            evaluation.setSearchable(searchable(searchRule, appliedSoFar));
            if (evaluation.isSearchable()) {
                bomToolProfiler.applicableStarted(bomTool);
                evaluation.setApplicable(bomTool.applicable());
                bomToolProfiler.applicableEnded(bomTool);
                if (evaluation.isApplicable()) {
                    appliedSoFar.add(bomTool);
                }
            }
        }
        return evaluations;
    }

    public BomToolResult searchable(final BomToolSearchRule searchRules, final List<BomTool> appliedSoFar) {
        final BomToolGroupType bomToolGroupType = searchRules.getBomTool().getBomToolGroupType();
        if (!environment.getBomToolFilter().shouldInclude(bomToolGroupType.toString())) {
            return new BomToolExcludedBomToolResult();
        }

        final int maxDepth = searchRules.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededBomToolResult(environment.getDepth(), maxDepth);
        }

        final Set<BomTool> yielded = appliedSoFar.stream()
                .filter(it -> searchRules.getYieldsTo().contains(it.getBomToolType()))
                .collect(Collectors.toSet());

        if (yielded.size() > 0) {
            return new YieldedBomToolResult(yielded);
        }

        final BomToolType bomToolType = searchRules.getBomTool().getBomToolType();
        final boolean nestable = searchRules.isNestable();
        if (environment.getForceNestedSearch()) {
            return new ForcedNestedPassedBomToolResult();
        } else if (nestable) {
            if (environment.getAppliedToParent().contains(bomToolType)) {
                return new NotSelfNestableBomToolResult();
            }
        } else if (!nestable && environment.getAppliedToParent().size() > 0) {
            return new NotNestableBomToolResult();
        }

        return new PassedBomToolResult();
    }
}
