/**
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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public abstract class Evaluator {
    private List<Consumer<DetectorAggregateEvaluationResult>> endCallbacks;
    private DetectorEvaluatorListener detectorEvaluatorListener;
    private final DetectorEvaluationOptions evaluationOptions;

    public Evaluator(DetectorEvaluationOptions evaluationOptions) {
        endCallbacks = new LinkedList<>();
        this.evaluationOptions = evaluationOptions;
    }

    public DetectorAggregateEvaluationResult evaluate(DetectorEvaluationTree rootEvaluation) {
        DetectorEvaluationTree evaluationTree = performEvaluation(rootEvaluation);
        DetectorAggregateEvaluationResult result = new DetectorAggregateEvaluationResult(evaluationTree);
        executeResultCallbacks(result);
        return result;
    }

    public void registerEvaluatorResultCallback(Consumer<DetectorAggregateEvaluationResult> callback) {
        endCallbacks.add(callback);
    }

    protected abstract DetectorEvaluationTree performEvaluation(DetectorEvaluationTree rootEvaluation);

    private void executeResultCallbacks(DetectorAggregateEvaluationResult evaluationResult) {
        for (Consumer<DetectorAggregateEvaluationResult> callback : endCallbacks) {
            callback.accept(evaluationResult);
        }
    }

    public DetectorEvaluationOptions getEvaluationOptions() {
        return evaluationOptions;
    }

    public Optional<DetectorEvaluatorListener> getDetectorEvaluatorListener() {
        return Optional.ofNullable(detectorEvaluatorListener);
    }

    public void setDetectorEvaluatorListener(DetectorEvaluatorListener detectorEvaluatorListener) {
        this.detectorEvaluatorListener = detectorEvaluatorListener;
    }
}
