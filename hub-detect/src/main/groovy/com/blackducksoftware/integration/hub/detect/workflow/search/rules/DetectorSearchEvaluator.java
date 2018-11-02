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

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExcludedDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ForcedNestedPassedDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.MaxDepthExceededDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.NotNestableDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.NotSelfNestableDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.YieldedDetectorResult;

public class DetectorSearchEvaluator {

    public List<DetectorEvaluation> evaluate(DetectorSearchRuleSet rules, EventSystem eventSystem) {
        final List<DetectorEvaluation> evaluations = new ArrayList<>();
        final List<Detector> appliedSoFar = new ArrayList<>();
        for (final DetectorSearchRule searchRule : rules.getOrderedBomToolRules()) {
            final Detector detector = searchRule.getDetector();
            final DetectorEvaluation evaluation = new DetectorEvaluation(detector, rules.getEnvironment());
            evaluations.add(evaluation);
            evaluation.setSearchable(searchable(searchRule, appliedSoFar, rules.getEnvironment()));
            if (evaluation.isSearchable()) {
                eventSystem.publishEvent(Event.ApplicableStarted, detector);
                evaluation.setApplicable(detector.applicable());
                eventSystem.publishEvent(Event.ApplicableEnded, detector);
                if (evaluation.isApplicable()) {
                    appliedSoFar.add(detector);
                }
            }
        }
        return evaluations;
    }

    public DetectorResult searchable(final DetectorSearchRule searchRules, final List<Detector> appliedSoFar, DetectorEnvironment environment) {
        Detector detector = searchRules.getDetector();
        final DetectorType detectorType = detector.getDetectorType();
        if (!environment.getBomToolFilter().shouldInclude(detectorType.toString())) {
            return new ExcludedDetectorResult();
        }

        final int maxDepth = searchRules.getMaxDepth();
        if (environment.getDepth() > maxDepth) {
            return new MaxDepthExceededDetectorResult(environment.getDepth(), maxDepth);
        }

        final Set<Detector> yieldTo = appliedSoFar.stream()
                                          .filter(it -> searchRules.getYieldsTo().contains(it))
                                          .collect(Collectors.toSet());

        if (yieldTo.size() > 0) {
            return new YieldedDetectorResult(yieldTo);
        }

        final boolean nestable = searchRules.isNestable();
        if (environment.getForceNestedSearch()) {
            return new ForcedNestedPassedDetectorResult();
        } else if (nestable) {
            if (environment.getAppliedToParent().stream().anyMatch(applied -> applied.isSame(detector))) {
                return new NotSelfNestableDetectorResult();
            }
        } else if (!nestable && environment.getAppliedToParent().size() > 0) {
            return new NotNestableDetectorResult();
        }

        return new PassedDetectorResult();
    }
}
