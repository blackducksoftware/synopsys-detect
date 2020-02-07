/**
 * detector
 *
 * Copyright (c) 2020 Synopsys, Inc.
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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorRuleSetEvaluatorTest {

    @Test
    public void test() {

        final DetectorRuleSet detectorRuleSet = Mockito.mock(DetectorRuleSet.class);
        final DetectorRule detectorRule = Mockito.mock(DetectorRule.class);
        final SearchEnvironment environment = Mockito.mock(SearchEnvironment.class);

        final Predicate<DetectorRule> rulePredicate = it -> true;
        Mockito.when(environment.getDetectorFilter()).thenReturn(rulePredicate);
        Mockito.when(detectorRule.getMaxDepth()).thenReturn(1);
        Mockito.when(environment.getDepth()).thenReturn(0);
        final Set<DetectorRule> appliedSoFar = new HashSet<>();
        Mockito.when(environment.getAppliedSoFar()).thenReturn(appliedSoFar);
        Mockito.when(detectorRule.isNestable()).thenReturn(true);
        Mockito.when(environment.isForceNestedSearch()).thenReturn(false);

        final DetectorRuleSetEvaluator evaluator = new DetectorRuleSetEvaluator();
        final DetectorResult result = evaluator.evaluateSearchable(detectorRuleSet, detectorRule, environment);

        assertTrue(result.getPassed());
    }
}
