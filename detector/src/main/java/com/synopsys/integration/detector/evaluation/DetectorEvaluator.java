/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.evaluation;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetectorEvaluator {
    private DetectorEvaluatorListener detectorEvaluatorListener;
    private ApplicableEvaluator applicableEvaluator;
    private ExtractableEvaluator extractableEvaluator;
    private ExtractionEvaluator extractionEvaluator;

    public DetectorEvaluator(DetectorEvaluationOptions evaluationOptions, Function<DetectorEvaluation, ExtractionEnvironment> extractionEnvironmentProvider) {
        applicableEvaluator = new ApplicableEvaluator(evaluationOptions);
        extractableEvaluator = new ExtractableEvaluator(evaluationOptions, extractionEnvironmentProvider);
        extractionEvaluator = new ExtractionEvaluator(evaluationOptions);
    }

    public DetectorAggregateEvaluationResult evaluate(DetectorEvaluationTree rootEvaluation) {
        // each evaluator mutates the rootEvaluation object's state.  So we only need to return the rootEvaluation object at the end.
        applicableEvaluator.evaluate(rootEvaluation);
        extractableEvaluator.evaluate(rootEvaluation);
        extractionEvaluator.evaluate(rootEvaluation);

        return new DetectorAggregateEvaluationResult(rootEvaluation);
    }

    public void registerPostApplicableCallback(Consumer<DetectorAggregateEvaluationResult> callBack) {
        applicableEvaluator.registerEvaluatorResultCallback(callBack);
    }

    public void registerPostExtractableCallback(Consumer<DetectorAggregateEvaluationResult> callBack) {
        extractableEvaluator.registerEvaluatorResultCallback(callBack);
    }

    public void registerPostExtractionCallback(Consumer<DetectorAggregateEvaluationResult> callBack) {
        extractionEvaluator.registerEvaluatorResultCallback(callBack);
    }

    public Optional<DetectorEvaluatorListener> getDetectorEvaluatorListener() {
        return Optional.ofNullable(detectorEvaluatorListener);
    }

    public void setDetectorEvaluatorListener(DetectorEvaluatorListener detectorEvaluatorListener) {
        this.detectorEvaluatorListener = detectorEvaluatorListener;
        applicableEvaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        extractableEvaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
        extractionEvaluator.setDetectorEvaluatorListener(detectorEvaluatorListener);
    }
}
