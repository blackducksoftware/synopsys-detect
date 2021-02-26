/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
