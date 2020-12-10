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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.Discovery;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class ExtractionEvaluator extends Evaluator {
    private Logger logger = LoggerFactory.getLogger(ExtractionEvaluator.class);

    public ExtractionEvaluator(DetectorEvaluationOptions evaluationOptions) {
        super(evaluationOptions);
    }

    @Override
    protected DetectorEvaluationTree performEvaluation(DetectorEvaluationTree rootEvaluation) {
        logger.debug("Starting detector extraction.");
        extractionEvaluation(rootEvaluation);
        return rootEvaluation;
    }

    public void extractionEvaluation(DetectorEvaluationTree detectorEvaluationTree) {
        logger.trace("Extracting detectors in the directory: {}", detectorEvaluationTree.getDirectory());
        for (DetectorEvaluation detectorEvaluation : detectorEvaluationTree.getOrderedEvaluations()) {
            if (detectorEvaluation.isExtractable() && detectorEvaluation.getExtractionEnvironment() != null) {

                logger.trace("Detector was searchable, applicable and extractable, will perform extraction: {}", detectorEvaluation.getDetectorRule().getDescriptiveName());
                Detectable detectable = detectorEvaluation.getDetectable();

                getDetectorEvaluatorListener().ifPresent(it -> it.extractionStarted(detectorEvaluation));

                Discovery discovery = detectorEvaluation.getDiscovery();
                if (discovery != null && discovery.getExtraction() != null) {
                    logger.debug("Extraction already completed during project discovery.");
                    detectorEvaluation.setExtraction(discovery.getExtraction());
                } else {
                    try {
                        Extraction extraction = detectable.extract(detectorEvaluation.getExtractionEnvironment());
                        detectorEvaluation.setExtraction(extraction);
                    } catch (Exception e) {
                        detectorEvaluation.setExtraction(new Extraction.Builder().exception(e).build());
                    }
                }

                getDetectorEvaluatorListener().ifPresent(it -> it.extractionEnded(detectorEvaluation));

                logger.trace("Extraction result: {}", detectorEvaluation.wasExtractionSuccessful());

            }
        }

        for (DetectorEvaluationTree childDetectorEvaluationTree : detectorEvaluationTree.getChildren()) {
            extractionEvaluation(childDetectorEvaluationTree);
        }
    }
}
