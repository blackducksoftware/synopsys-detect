/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.evaluation;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExceptionDetectableResult;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.result.DetectorResult;

public class ExtractableEvaluator extends Evaluator {
    private final Logger logger = LoggerFactory.getLogger(ExtractableEvaluator.class);
    private Function<DetectorEvaluation, ExtractionEnvironment> extractionEnvironmentProvider;

    public ExtractableEvaluator(DetectorEvaluationOptions evaluationOptions, Function<DetectorEvaluation, ExtractionEnvironment> extractionEnvironmentProvider) {
        super(evaluationOptions);
        this.extractionEnvironmentProvider = extractionEnvironmentProvider;
    }

    @Override
    protected DetectorEvaluationTree performEvaluation(DetectorEvaluationTree rootEvaluation) {
        extractableEvaluation(rootEvaluation);
        logger.debug("Preparing detectors for discovery and extraction.");
        setupDiscoveryAndExtractions(rootEvaluation, extractionEnvironmentProvider);
        return rootEvaluation;
    }

    public void extractableEvaluation(DetectorEvaluationTree detectorEvaluationTree) {
        logger.debug("Starting detector preparation.");
        logger.trace("Determining extractable detectors in the directory: {}", detectorEvaluationTree.getDirectory());
        for (DetectorEvaluation detectorEvaluation : detectorEvaluationTree.getOrderedEvaluations()) {
            if (detectorEvaluation.isSearchable() && detectorEvaluation.isApplicable()) {

                getDetectorEvaluatorListener().ifPresent(it -> it.extractableStarted(detectorEvaluation));

                logger.trace("Detector was searchable and applicable, will check extractable: {}", detectorEvaluation.getDetectorRule().getDescriptiveName());

                logger.trace("Checking to see if this detector is a fallback detector.");
                DetectableResult detectableExtractableResult = getDetectableExtractableResult(detectorEvaluation);

                DetectorResult extractableResult = new DetectorResult(detectableExtractableResult.getPassed(), detectableExtractableResult.toDescription(), detectableExtractableResult.getClass(),
                    detectableExtractableResult.getExplanation(), detectableExtractableResult.getRelevantFiles());
                detectorEvaluation.setExtractable(extractableResult);
                if (detectorEvaluation.isExtractable()) {
                    logger.trace("Extractable passed. Done evaluating for now.");
                } else {
                    logger.trace("Extractable did not pass: {}", detectorEvaluation.getExtractabilityMessage());
                }

                getDetectorEvaluatorListener().ifPresent(it -> it.extractableEnded(detectorEvaluation));
            }
        }

        for (DetectorEvaluationTree childDetectorEvaluationTree : detectorEvaluationTree.getChildren()) {
            extractableEvaluation(childDetectorEvaluationTree);
        }
    }

    private DetectableResult getDetectableExtractableResult(DetectorEvaluation detectorEvaluation) {
        Detectable detectable = detectorEvaluation.getDetectable();
        try {
            return detectable.extractable();
        } catch (DetectableException e) {
            return new ExceptionDetectableResult(e);
        }
    }

    public void setupDiscoveryAndExtractions(DetectorEvaluationTree detectorEvaluationTree, Function<DetectorEvaluation, ExtractionEnvironment> extractionEnvironmentProvider) {
        for (DetectorEvaluation detectorEvaluation : detectorEvaluationTree.getOrderedEvaluations()) {
            if (detectorEvaluation.isExtractable()) {
                ExtractionEnvironment extractionEnvironment = extractionEnvironmentProvider.apply(detectorEvaluation);
                detectorEvaluation.setExtractionEnvironment(extractionEnvironment);
            }
        }

        for (DetectorEvaluationTree childDetectorEvaluationTree : detectorEvaluationTree.getChildren()) {
            setupDiscoveryAndExtractions(childDetectorEvaluationTree, extractionEnvironmentProvider);
        }
    }
}
