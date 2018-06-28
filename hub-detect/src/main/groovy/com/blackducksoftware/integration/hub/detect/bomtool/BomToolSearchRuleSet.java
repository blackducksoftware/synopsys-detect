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
package com.blackducksoftware.integration.hub.detect.bomtool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolExcludedBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.ForcedNestedPassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.MaxDepthExceededBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.NotNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.NotSelfNestableBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.YieldedBomToolResult;
import com.blackducksoftware.integration.hub.detect.manager.extraction.BomToolEvaluation;

public class BomToolSearchRuleSet {

    private final List<BomToolSearchRule> orderedBomToolRules;
    private final BomToolEnvironment environment;

    public BomToolSearchRuleSet(final List<BomToolSearchRule> orderedBomToolRules, final BomToolEnvironment environment) {
        this.orderedBomToolRules = orderedBomToolRules;
        this.environment = environment;
    }

    public List<BomToolEvaluation> evaluate() {
        final List<BomToolEvaluation> evaluations = new ArrayList<>();
        final List<BomTool> appliedSoFar = new ArrayList<>();
        for (final BomToolSearchRule searchRules : orderedBomToolRules) {
            final BomToolEvaluation evaluation = new BomToolEvaluation(searchRules.getBomTool(), environment);
            evaluations.add(evaluation);
            evaluation.setSearchable(searchable(searchRules, appliedSoFar));
            if (evaluation.isSearchable()) {
                evaluation.setApplicable(evaluation.getBomTool().applicable());
                if (evaluation.isApplicable()) {
                    appliedSoFar.add(searchRules.getBomTool());
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
