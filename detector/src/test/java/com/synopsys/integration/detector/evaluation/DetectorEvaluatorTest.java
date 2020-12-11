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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.function.Function;

import org.junit.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

//TODO: These tests should be re-written to use a concrete set of objects rather than mocks.
public class DetectorEvaluatorTest {

    @Test
    public void testEvaluation() {
        DetectorEvaluatorListener listener = Mockito.mock(DetectorEvaluatorListener.class);
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        ExtractionEnvironment extractionEnvironment = Mockito.mock(ExtractionEnvironment.class);
        Function<DetectorEvaluation, ExtractionEnvironment> extractionEnvironmentProvider = (detectorEvaluation) -> extractionEnvironment;
        DiscoveryFilter discoveryFilter = Mockito.mock(DiscoveryFilter.class);
        DetectorEvaluationTree rootEvaluation = Mockito.mock(DetectorEvaluationTree.class);

        DetectorEvaluator detectorEvaluator = new DetectorEvaluator(evaluationOptions, extractionEnvironmentProvider, discoveryFilter);
        detectorEvaluator.setDetectorEvaluatorListener(listener);
        DetectorAggregateEvaluationResult result = detectorEvaluator.evaluate(rootEvaluation);

        Optional<DetectorEvaluatorListener> actualListener = detectorEvaluator.getDetectorEvaluatorListener();
        assertTrue(actualListener.isPresent());
        assertEquals(listener, actualListener.get());
        assertEquals(rootEvaluation, result.getEvaluationTree());
    }

}
