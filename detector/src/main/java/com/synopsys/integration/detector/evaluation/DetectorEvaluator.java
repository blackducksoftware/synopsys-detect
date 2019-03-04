/**
 * detector
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExceptionDetectableResult;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.finder.DetectorFilter;
import com.synopsys.integration.detector.result.DetectableDetectorResult;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectorEvaluator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SearchableEvaluator searchableEvaluator;
    private DetectorFilter detectorFilter;
    private boolean forceNested;

    //Unfortunately, currently search and applicable are tied together due to Search needing to know about previous detectors that applied.
    //So Search and then Applicable must be evaluated of Detector 1 before the next Search can be evaluated of Detector 2.
    public void searchAndApplicableEvaluation(DetectorEvaluationTree detectorEvaluationTree, Set<DetectorRule> appliedInParent) {
        logger.info("Preparing to evaluate 'searchable and applicable' detectors on the directory: " + detectorEvaluationTree.getDirectory().toString());
        logger.info("The number of evaluations: " + detectorEvaluationTree.getOrderedEvaluations().size());
        logger.info("The number of children: " + detectorEvaluationTree.getChildren().size());

        Set<DetectorRule> appliedSoFar = new HashSet<>();

        for (DetectorEvaluation detectorEvaluation : detectorEvaluationTree.getOrderedEvaluations()){
            DetectorRule detectorRule = detectorEvaluation.getDetectorRule();
            logger.info("Evaluating detector: " + detectorRule.getDescriptiveName());
            SearchEnvironment searchEnvironment = new SearchEnvironment(detectorEvaluationTree.getDepthFromRoot(), detectorFilter, forceNested, appliedInParent, appliedSoFar);
            detectorEvaluation.setSearchEnvironment(searchEnvironment);
            DetectorResult searchableResult = searchableEvaluator.evaluate(detectorEvaluationTree.getDetectorRuleSet(), detectorEvaluation.getDetectorRule(), searchEnvironment);
            detectorEvaluation.setSearchable(searchableResult);

            if (detectorEvaluation.isSearchable()){
                logger.info("Searchable passed, will continue evaluating.");
                DetectableEnvironment detectableEnvironment = new DetectableEnvironment(detectorEvaluationTree.getDirectory()); //TODO: potential todo, this could be invoked as part of the rule (file could be given to the creatable and the creatable could create the env)
                detectorEvaluation.setDetectableEnvironment(detectableEnvironment);
                Detectable detectable = detectorRule.createDetectable(detectableEnvironment);
                detectorEvaluation.setDetectable(detectable);
                DetectorResult applicableResult = new DetectableDetectorResult(detectable.applicable());
                detectorEvaluation.setApplicable(applicableResult);
                if (detectorEvaluation.isApplicable()){
                    logger.info("Applicable passed. Will add to applicable list. Done evaluating for now.");
                    appliedSoFar.add(detectorRule);
                } else {
                    logger.info("Applicable did not pass, will not continue evaluating.");
                }
            } else {
                logger.info("Searchable did not pass, will not continue evaluating.");
            }
        }

        Set<DetectorRule> nextAppliedInParent = new HashSet<>();
        nextAppliedInParent.addAll(appliedInParent);
        nextAppliedInParent.addAll(appliedSoFar);

        for (DetectorEvaluationTree childDetectorEvaluationTree : detectorEvaluationTree.getChildren()){
            searchAndApplicableEvaluation(childDetectorEvaluationTree, appliedInParent);
        }
    }

    public void extractableEvaluation(DetectorEvaluationTree detectorEvaluationTree){
        logger.info("Preparing to evaluate 'extractable' detectors on the directory: " + detectorEvaluationTree.getDirectory().toString());
        logger.info("The number of evaluations: " + detectorEvaluationTree.getOrderedEvaluations().size());
        logger.info("The number of children: " + detectorEvaluationTree.getChildren().size());
        for (DetectorEvaluation detectorEvaluation : detectorEvaluationTree.getOrderedEvaluations()) {
            if (detectorEvaluation.isSearchable() && detectorEvaluation.isApplicable()){
                logger.info("Detector was searchable and applicable, will check extractable: " + detectorEvaluation.getDetectorRule().getDescriptiveName());
                Detectable detectable = detectorEvaluation.getDetectable();
                DetectableResult detectableExtractableResult;
                try {
                    detectableExtractableResult = detectable.extractable();
                } catch (DetectableException e) {
                    detectableExtractableResult = new ExceptionDetectableResult(e);
                }
                DetectorResult extractableResult = new DetectableDetectorResult(detectableExtractableResult);
                detectorEvaluation.setExtractable(extractableResult);
                if (detectorEvaluation.isExtractable()){
                    logger.info("Extractable passed. Done evaluating for now.");
                } else {
                    logger.info("Extractable did not pass, will not continue evaluating.");
                }
            }
        }

        for (DetectorEvaluationTree childDetectorEvaluationTree : detectorEvaluationTree.getChildren()){
            extractableEvaluation(childDetectorEvaluationTree);
        }
    }

    public void extractionEvaluation(DetectorEvaluationTree detectorEvaluationTree, Function<DetectorEvaluation, ExtractionEnvironment> extractionEnvironmentProvider){
        logger.info("Preparing to evaluate 'extractable' detectors on the directory: " + detectorEvaluationTree.getDirectory().toString());
        logger.info("The number of evaluations: " + detectorEvaluationTree.getOrderedEvaluations().size());
        logger.info("The number of children: " + detectorEvaluationTree.getChildren().size());
        for (DetectorEvaluation detectorEvaluation : detectorEvaluationTree.getOrderedEvaluations()) {
            if (detectorEvaluation.isSearchable() && detectorEvaluation.isApplicable() && detectorEvaluation.isExtractable()){
                logger.info("Detector was searchable, applicable and extractable, will perform extraction: " + detectorEvaluation.getDetectorRule().getDescriptiveName());
                Detectable detectable = detectorEvaluation.getDetectable();
                ExtractionEnvironment extractionEnvironment = extractionEnvironmentProvider.apply(detectorEvaluation);
                detectorEvaluation.setExtractionEnvironment(extractionEnvironment);
                Extraction extraction = detectable.extract(extractionEnvironment);
                detectorEvaluation.setExtraction(extraction);
                logger.info("Extraction result: " + detectorEvaluation.wasExtractionSuccessful());
            }
        }

        for (DetectorEvaluationTree childDetectorEvaluationTree : detectorEvaluationTree.getChildren()){
            extractionEvaluation(childDetectorEvaluationTree, extractionEnvironmentProvider);
        }
    }


}
