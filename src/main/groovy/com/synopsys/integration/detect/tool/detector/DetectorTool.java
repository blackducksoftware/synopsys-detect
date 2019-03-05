/**
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.evaluation.DetectorEvaluator;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.finder.DetectorFinderDirectoryListException;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectContext detectContext;

    public DetectorTool(final DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public DetectorToolResult performDetectors(final File directory, final DetectorFinderOptions detectorFinderOptions, final String projectBomTool) throws DetectUserFriendlyException {
        logger.info("Preparing to initialize detectors.");

        final DetectableFactory detectableFactory = detectContext.getBean(DetectableFactory.class);
        final DetectorFinder detectorFinder = new DetectorFinder();
        final DetectorRuleFactory detectorRuleFactory = new DetectorRuleFactory();
        final DetectorRuleSet detectRuleSet = detectorRuleFactory.createRules(detectableFactory, false); // TODO: Use config

        final DetectorEvaluationTree rootEvaluation;
        try {
            logger.info("Finding detectors.");
            rootEvaluation = detectorFinder.findDetectors(directory, detectRuleSet, detectorFinderOptions);
        } catch (final DetectorFinderDirectoryListException e) {
            throw new DetectUserFriendlyException("Detect was unable to list a directory while searching for detectors.", e, ExitCodeType.FAILURE_DETECTOR);
        }

        logger.info("Evaluating search and applicable.");
        final DetectorEvaluator detectorEvaluator = new DetectorEvaluator();
        detectorEvaluator.searchAndApplicableEvaluation(rootEvaluation, new HashSet<>());
        logger.info("Evaluating extractable.");
        detectorEvaluator.extractableEvaluation(rootEvaluation);
        logger.info("Evaluating extractions.");
        detectorEvaluator.extractionEvaluation(rootEvaluation, detectorEvaluation -> new ExtractionEnvironment(new File("")));

        //Completed.
        logger.info("Extractions finished.");
        //detectors

        //        logger.info("Preparing to initialize detectors.");
        //        DetectorFactory detectorFactory = detectContext.getBean(DetectorFactory.class);
        //        EventSystem eventSystem = detectContext.getBean(EventSystem.class);
        //
        //        logger.info("Building detector system.");
        //        DetectorSearchProvider detectorSearchProvider = new DetectorSearchProvider(detectorFactory);
        //        SearchableEvaluator detectorSearchEvaluator = new SearchableEvaluator();
        //
        //        SearchManager searchManager = new SearchManager(searchOptions, detectorSearchProvider, detectorSearchEvaluator, eventSystem);
        //        PreparationManager preparationManager = new PreparationManager(eventSystem);
        //        ExtractionManager extractionManager = new ExtractionManager();
        //
        //        DetectorManager detectorManager = new DetectorManager(searchManager, extractionManager, preparationManager, eventSystem);
        //        logger.info("Running detectors.");
        //        DetectorToolResult detectorToolResult = detectorManager.runDetectors();
        //        logger.info("Finished running detectors.");
        //        eventSystem.publishEvent(Event.DetectorsComplete, detectorToolResult);
        //
        //        logger.info("Evaluating detectors for project info.");
        //
        //        DetectorEvaluationNameVersionDecider detectorEvaluationNameVersionDecider = new DetectorEvaluationNameVersionDecider(new DetectorNameVersionDecider());
        //        Optional<NameVersion> bomToolNameVersion = detectorEvaluationNameVersionDecider.decideSuggestion(detectorToolResult.evaluatedDetectors, projectBomTool);
        //        detectorToolResult.bomToolProjectNameVersion = bomToolNameVersion;
        //        logger.info("Finished evaluating detectors for project info.");

        return null; //TODO: Fix
    }
}
