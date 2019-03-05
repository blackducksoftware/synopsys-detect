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
package com.synopsys.integration.detector.evaluation;

import java.util.HashSet;
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

public class SearchableEvaluator { //TODO: rename? is not what i want an evaluator to be.
    public DetectorResult evaluate(DetectorRuleSet detectorRuleSet, DetectorRule detectorRule, SearchEnvironment environment) {
        if (!environment.getDetectorFilter().shouldInclude(detectorRule.getDetectorType())) {
            return new ExcludedDetectorResult();
        }

        final int maxDepth = detectorRule.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededDetectorResult(environment.getDepth(), maxDepth);
        }

        Set<DetectorRule> applied = new HashSet<>();
        applied.addAll(environment.getAppliedToParent());
        applied.addAll(environment.getAppliedSoFar());

        final Set<DetectorRule> yieldTo = applied.stream()
                                          .filter(it -> detectorRuleSet.getYieldsTo(detectorRule).contains(it))
                                          .collect(Collectors.toSet());

        if (yieldTo.size() > 0) {
            return new YieldedDetectorResult(yieldTo);
        }

        final boolean nestable = detectorRule.isNestable();
        if (environment.isForceNestedSearch()) {
            return new ForcedNestedPassedDetectorResult();
        } else if (nestable) {
            if (environment.getAppliedToParent().stream().anyMatch(parentApplied -> parentApplied.equals(this))) {
                return new NotSelfNestableDetectorResult();
            }
        } else if (!nestable && environment.getAppliedToParent().size() > 0) {
            return new NotNestableDetectorResult();
        }

        return new PassedDetectorResult();
    }
}
