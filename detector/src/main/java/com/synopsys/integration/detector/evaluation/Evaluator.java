package com.synopsys.integration.detector.evaluation;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public abstract class Evaluator {
    private List<Function<DetectorAggregateEvaluationResult, Void>> endCallbacks;
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

    public void registerEvaluatorResultCallback(Function<DetectorAggregateEvaluationResult, Void> callback) {
        endCallbacks.add(callback);
    }

    protected abstract DetectorEvaluationTree performEvaluation(DetectorEvaluationTree rootEvaluation);

    private void executeResultCallbacks(DetectorAggregateEvaluationResult evaluationResult) {
        for (Function<DetectorAggregateEvaluationResult, Void> callback : endCallbacks) {
            callback.apply(evaluationResult);
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
