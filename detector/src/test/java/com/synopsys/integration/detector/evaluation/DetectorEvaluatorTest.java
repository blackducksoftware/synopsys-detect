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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetectorEvaluatorTest {

    @Test
    public void testEvaluation() {
        DetectorEvaluatorListener listener = Mockito.mock(DetectorEvaluatorListener.class);
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        DetectorEvaluationTree rootEvaluation = Mockito.mock(DetectorEvaluationTree.class);
        List<Evaluator> evaluatorList = new ArrayList<>();
        Evaluator evaluator = new Evaluator(evaluationOptions) {
            @Override
            protected DetectorEvaluationTree performEvaluation(DetectorEvaluationTree rootEvaluation) {
                return rootEvaluation;
            }
        };
        evaluatorList.add(evaluator);
        DetectorEvaluator detectorEvaluator = new DetectorEvaluator(evaluationOptions, evaluatorList);
        detectorEvaluator.setDetectorEvaluatorListener(listener);
        DetectorAggregateEvaluationResult result = detectorEvaluator.evaluate(rootEvaluation);

        Optional<DetectorEvaluatorListener> actualListener = detectorEvaluator.getDetectorEvaluatorListener();
        assertTrue(actualListener.isPresent());
        assertEquals(listener, actualListener.get());
        assertEquals(rootEvaluation, result.getEvaluationTree());
        assertTrue(evaluator.getDetectorEvaluatorListener().isPresent());
        assertEquals(listener, evaluator.getDetectorEvaluatorListener().get());
    }

}
