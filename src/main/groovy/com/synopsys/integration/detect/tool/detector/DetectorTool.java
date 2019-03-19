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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.impl.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.project.DetectorEvaluationNameVersionDecider;
import com.synopsys.integration.detect.workflow.project.DetectorNameVersionDecider;
import com.synopsys.integration.detect.workflow.status.DetectorStatus;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.evaluation.DetectorEvaluationOptions;
import com.synopsys.integration.detector.evaluation.DetectorEvaluator;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.finder.DetectorFinderDirectoryListException;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.util.NameVersion;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExtractionEnvironmentProvider extractionEnvironmentProvider;
    private final EventSystem eventSystem;
    private final CodeLocationConverter codeLocationConverter;

    public DetectorTool(final ExtractionEnvironmentProvider extractionEnvironmentProvider, final EventSystem eventSystem, final CodeLocationConverter codeLocationConverter) {
        this.extractionEnvironmentProvider = extractionEnvironmentProvider;
        this.eventSystem = eventSystem;
        this.codeLocationConverter = codeLocationConverter;
    }

    public DetectorToolResult performDetectors(final File directory, DetectorRuleSet detectorRuleSet, final DetectorFinderOptions detectorFinderOptions, DetectorEvaluationOptions evaluationOptions, final String projectBomTool) throws DetectUserFriendlyException {
        logger.info("Initializing detector system.");

        final DetectorFinder detectorFinder = new DetectorFinder();
        final Optional<DetectorEvaluationTree> possibleRootEvaluation;
        try {
            logger.info("Starting detector file system traversal.");
            possibleRootEvaluation = detectorFinder.findDetectors(directory, detectorRuleSet, detectorFinderOptions);

        } catch (final DetectorFinderDirectoryListException e) {
            throw new DetectUserFriendlyException("Detect was unable to list a directory while searching for detectors.", e, ExitCodeType.FAILURE_DETECTOR);
        }

        if (!possibleRootEvaluation.isPresent()) {
            logger.error("The source directory could not be searched for detectors - detector tool failed.");
            logger.error("Please ensure the provided source path is a directory and detect has access.");
            eventSystem.publishEvent(Event.StatusSummary, new Status("DETECTOR", StatusType.FAILURE));
            return new DetectorToolResult();
        }

        final DetectorEvaluationTree rootEvaluation = possibleRootEvaluation.get();
        final List<DetectorEvaluation> detectorEvaluations = rootEvaluation.allDescendentEvaluations();

        logger.trace("Setting up detector events.");
        final DetectorEvaluatorBroadcaster eventBroadcaster = new DetectorEvaluatorBroadcaster(eventSystem);
        final DetectorEvaluator detectorEvaluator = new DetectorEvaluator(evaluationOptions);
        detectorEvaluator.setDetectorEvaluatorListener(eventBroadcaster);

        logger.info("Starting detector evaluations.");
        detectorEvaluator.searchAndApplicableEvaluation(rootEvaluation, new HashSet<>());

        Set<DetectorType> applicable = detectorEvaluations.stream()
                                                         .filter(DetectorEvaluation::isApplicable)
                                                         .map(DetectorEvaluation::getDetectorRule)
                                                         .map(DetectorRule::getDetectorType)
                                                         .collect(Collectors.toSet());

        eventSystem.publishEvent(Event.ApplicableCompleted, applicable);
        eventSystem.publishEvent(Event.SearchCompleted, rootEvaluation);

        logger.info("Starting detector preparation.");
        detectorEvaluator.extractableEvaluation(rootEvaluation);
        eventSystem.publishEvent(Event.PreparationsCompleted, rootEvaluation);

        logger.info("Starting detector extraction.");
        final Integer extractionCount = Math.toIntExact(detectorEvaluations.stream()
                                                            .filter(DetectorEvaluation::isExtractable)
                                                            .count());
        eventSystem.publishEvent(Event.ExtractionCount, extractionCount);

        logger.info("Total number of extractions: " + extractionCount);

        detectorEvaluator.extractionEvaluation(rootEvaluation, extractionEnvironmentProvider::createExtractionEnvironment);
        eventSystem.publishEvent(Event.ExtractionsCompleted, rootEvaluation);

        final Map<DetectorType, StatusType> statusMap = extractStatus(detectorEvaluations);
        statusMap.forEach((detectorType, statusType) -> eventSystem.publishEvent(Event.StatusSummary, new DetectorStatus(detectorType, statusType)));
        if (statusMap.containsValue(StatusType.FAILURE)){
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_DETECTOR, "One or more detectors were not succesfull."));
        }

        logger.info("Finished extractions.");

        final DetectorToolResult detectorToolResult = new DetectorToolResult();

        detectorToolResult.rootDetectorEvaluationTree = Optional.of(rootEvaluation);

        detectorToolResult.applicableDetectorTypes = applicable;

        detectorToolResult.codeLocationMap = detectorEvaluations.stream()
                                                 .filter(DetectorEvaluation::wasExtractionSuccessful)
                                                 .map(it -> codeLocationConverter.toDetectCodeLocation(directory, it))
                                                 .map(Map::entrySet)
                                                 .flatMap(Collection::stream)
                                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        detectorToolResult.bomToolCodeLocations = new ArrayList<>(detectorToolResult.codeLocationMap.values());

        final DetectorEvaluationNameVersionDecider detectorEvaluationNameVersionDecider = new DetectorEvaluationNameVersionDecider(new DetectorNameVersionDecider());
        final Optional<NameVersion> bomToolNameVersion = detectorEvaluationNameVersionDecider.decideSuggestion(detectorEvaluations, projectBomTool);
        detectorToolResult.bomToolProjectNameVersion = bomToolNameVersion;
        logger.info("Finished evaluating detectors for project info.");

        //Completed.
        logger.info("Finished running detectors.");
        eventSystem.publishEvent(Event.DetectorsComplete, detectorToolResult);

        return detectorToolResult;
    }

    private Map<DetectorType, StatusType> extractStatus(final List<DetectorEvaluation> detectorEvaluations) {
        final Map<DetectorType, StatusType> statusMap = new HashMap<>();
        for (final DetectorEvaluation detectorEvaluation : detectorEvaluations) {
            final DetectorType detectorType = detectorEvaluation.getDetectorRule().getDetectorType();
            if (detectorEvaluation.isApplicable()) {
                final StatusType statusType;
                if (detectorEvaluation.isExtractable()) {
                    if (detectorEvaluation.wasExtractionSuccessful()) {
                        statusType = StatusType.SUCCESS;
                    } else if (detectorEvaluation.wasExtractionFailure()) {
                        statusType = StatusType.FAILURE;
                    } else if (detectorEvaluation.wasExtractionException()) {
                        statusType = StatusType.FAILURE;
                    } else {
                        logger.warn("An issue occured in the detector system, an unknown evaluation status was created. Please don't do this again.");
                        statusType = StatusType.FAILURE;
                    }
                } else {
                    statusType = StatusType.FAILURE;
                }
                if (statusType == StatusType.FAILURE || !statusMap.containsKey(detectorType)){
                    statusMap.put(detectorType, statusType);
                }
            }
        }
        return statusMap;
    }
}
