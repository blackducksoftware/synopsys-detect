/*
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
package com.synopsys.integration.detector.evaluation;

import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.result.ExcludedDetectorResult;
import com.synopsys.integration.detector.result.ForcedNestedPassedDetectorResult;
import com.synopsys.integration.detector.result.MaxDepthExceededDetectorResult;
import com.synopsys.integration.detector.result.NotNestableDetectorResult;
import com.synopsys.integration.detector.result.NotSelfNestableDetectorResult;
import com.synopsys.integration.detector.result.PassedDetectorResult;
import com.synopsys.integration.detector.result.YieldedDetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorRuleSetEvaluator {
    public DetectorResult evaluateSearchable(final DetectorRuleSet detectorRuleSet, final DetectorRule detectorRule, final SearchEnvironment environment) {
        if (!environment.getDetectorFilter().test(detectorRule)) {
            return new ExcludedDetectorResult();
        }

        final int maxDepth = detectorRule.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededDetectorResult(environment.getDepth(), maxDepth);
        }

        final Set<DetectorRule> yieldTo = environment.getAppliedSoFar().stream()
                                              .filter(it -> detectorRuleSet.getYieldsTo(detectorRule).contains(it))
                                              .collect(Collectors.toSet());

        if (yieldTo.size() > 0) {
            return new YieldedDetectorResult(yieldTo.stream().map(DetectorRule::getName).collect(Collectors.toSet()));
        }

        final boolean nestable = detectorRule.isNestable();
        final boolean selfNestable = detectorRule.isSelfNestable();
        if (environment.isForceNestedSearch()) {
            return new ForcedNestedPassedDetectorResult();
        } else if (nestable) {
            if (!selfNestable && environment.getAppliedToParent().stream().anyMatch(parentApplied -> parentApplied.equals(detectorRule))) {
                return new NotSelfNestableDetectorResult();
            }
        } else if (environment.getAppliedToParent().stream().anyMatch(it -> !it.isNestInvisible())) {
            return new NotNestableDetectorResult();
        }

        return new PassedDetectorResult();
    }
}
