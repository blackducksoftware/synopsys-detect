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

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetectorEvaluator extends Evaluator {
    private DetectorEvaluatorListener detectorEvaluatorListener;
    private final List<Evaluator> evaluators;

    public DetectorEvaluator(DetectorEvaluationOptions evaluationOptions, List<Evaluator> evaluators) {
        super(evaluationOptions);
        this.evaluators = evaluators;
    }

    @Override
    protected DetectorEvaluationTree performEvaluation(DetectorEvaluationTree rootEvaluation) {
        DetectorEvaluationTree currentEvaluationTree = rootEvaluation;
        for (Evaluator evaluator : evaluators) {
            currentEvaluationTree = evaluator.evaluate(currentEvaluationTree).getEvaluationTree();
        }
        return currentEvaluationTree;
    }

    @Override
    public Optional<DetectorEvaluatorListener> getDetectorEvaluatorListener() {
        return Optional.ofNullable(detectorEvaluatorListener);
    }

    @Override
    public void setDetectorEvaluatorListener(DetectorEvaluatorListener detectorEvaluatorListener) {
        this.detectorEvaluatorListener = detectorEvaluatorListener;
        for (Evaluator evaluator : evaluators) {
            evaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        }
    }
}
