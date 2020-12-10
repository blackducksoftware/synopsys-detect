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
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DiscoveryEvaluator extends Evaluator {
    private final Logger logger = LoggerFactory.getLogger(DiscoveryEvaluator.class);
    private final DiscoveryFilter discoveryFilter;

    public DiscoveryEvaluator(DetectorEvaluationOptions evaluationOptions, DiscoveryFilter discoveryFilter) {
        super(evaluationOptions);
        this.discoveryFilter = discoveryFilter;
    }

    @Override
    protected DetectorEvaluationTree performEvaluation(DetectorEvaluationTree rootEvaluation) {
        logger.debug("Starting detector project discovery.");
        discoveryEvaluation(rootEvaluation, discoveryFilter);
        return rootEvaluation;
    }

    private void discoveryEvaluation(DetectorEvaluationTree detectorEvaluationTree, DiscoveryFilter discoveryFilter) {
        logger.trace("Project discovery started.");

        logger.trace("Determining discoverable detectors in the directory: {}", detectorEvaluationTree.getDirectory());
        for (DetectorEvaluation detectorEvaluation : detectorEvaluationTree.getOrderedEvaluations()) {
            if (detectorEvaluation.isExtractable() && detectorEvaluation.getExtractionEnvironment() != null) {

                logger.trace("Detector was searchable, applicable and extractable, will perform project discovery: {}", detectorEvaluation.getDetectorRule().getDescriptiveName());
                Detectable detectable = detectorEvaluation.getDetectable();

                getDetectorEvaluatorListener().ifPresent(it -> it.discoveryStarted(detectorEvaluation));

                if (discoveryFilter.shouldDiscover(detectorEvaluation)) {
                    try {
                        Discovery discovery = detectable.discover(detectorEvaluation.getExtractionEnvironment());
                        detectorEvaluation.setDiscovery(discovery);
                    } catch (Exception e) {
                        detectorEvaluation.setDiscovery(new Discovery.Builder().exception(e).build());
                    }
                } else {
                    logger.debug("Project discovery already found information, this detector will be skipped.");
                    detectorEvaluation.setDiscovery(new Discovery.Builder().skipped().build());
                }

                getDetectorEvaluatorListener().ifPresent(it -> it.discoveryEnded(detectorEvaluation));

            }
        }

        for (DetectorEvaluationTree childDetectorEvaluationTree : detectorEvaluationTree.getChildren()) {
            discoveryEvaluation(childDetectorEvaluationTree, discoveryFilter);
        }
    }
}
