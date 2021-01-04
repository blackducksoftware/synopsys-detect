/**
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.nameversion.DetectorNameVersionHandler;
import com.synopsys.integration.detect.workflow.nameversion.PreferredDetectorNameVersionHandler;
import com.synopsys.integration.detect.workflow.status.DetectorStatus;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.evaluation.DetectorAggregateEvaluationResult;
import com.synopsys.integration.detector.evaluation.DetectorEvaluationOptions;
import com.synopsys.integration.detector.evaluation.DetectorEvaluator;
import com.synopsys.integration.detector.evaluation.DiscoveryFilter;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.finder.DetectorFinderDirectoryListException;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectorFinder detectorFinder;
    private final ExtractionEnvironmentProvider extractionEnvironmentProvider;
    private final EventSystem eventSystem;
    private final CodeLocationConverter codeLocationConverter;
    private final DetectorIssuePublisher detectorIssuePublisher;

    public DetectorTool(DetectorFinder detectorFinder, ExtractionEnvironmentProvider extractionEnvironmentProvider, EventSystem eventSystem, CodeLocationConverter codeLocationConverter,
        DetectorIssuePublisher detectorIssuePublisher) {
        this.detectorFinder = detectorFinder;
        this.extractionEnvironmentProvider = extractionEnvironmentProvider;
        this.eventSystem = eventSystem;
        this.codeLocationConverter = codeLocationConverter;
        this.detectorIssuePublisher = detectorIssuePublisher;
    }

    public DetectorToolResult performDetectors(File directory, DetectorRuleSet detectorRuleSet, DetectorFinderOptions detectorFinderOptions, DetectorEvaluationOptions evaluationOptions, String projectDetector,
        List<DetectorType> requiredDetectors)
        throws DetectUserFriendlyException {
        logger.debug("Initializing detector system.");
        Optional<DetectorEvaluationTree> possibleRootEvaluation;
        try {
            logger.debug("Starting detector file system traversal.");
            possibleRootEvaluation = detectorFinder.findDetectors(directory, detectorRuleSet, detectorFinderOptions);

        } catch (DetectorFinderDirectoryListException e) {
            throw new DetectUserFriendlyException("Detect was unable to list a directory while searching for detectors.", e, ExitCodeType.FAILURE_DETECTOR);
        }

        if (!possibleRootEvaluation.isPresent()) {
            logger.error("The source directory could not be searched for detectors - detector tool failed.");
            logger.error("Please ensure the provided source path is a directory and detect has access.");
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION, "Detector tool failed to run on the configured source path."));
            return new DetectorToolResult();
        }

        DetectorEvaluationTree rootEvaluation = possibleRootEvaluation.get();
        List<DetectorEvaluation> detectorEvaluations = rootEvaluation.allDescendentEvaluations();

        logger.trace("Setting up detector events.");
        DetectorNameVersionHandler detectorNameVersionHandler = createNameVersionHandler(projectDetector);
        DiscoveryFilter discoveryFilter = new DetectDiscoveryFilter(eventSystem, detectorNameVersionHandler);
        DetectorEvaluatorBroadcaster eventBroadcaster = new DetectorEvaluatorBroadcaster(eventSystem);

        DetectorEvaluator detectorEvaluator = new DetectorEvaluator(evaluationOptions, extractionEnvironmentProvider::createExtractionEnvironment, discoveryFilter);
        detectorEvaluator.setDetectorEvaluatorListener(eventBroadcaster);

        detectorEvaluator.registerPostApplicableCallback(detectorAggregateEvaluationResult -> {
            eventSystem.publishEvent(Event.ApplicableCompleted, detectorAggregateEvaluationResult.getApplicableDetectorTypes());
            eventSystem.publishEvent(Event.SearchCompleted, detectorAggregateEvaluationResult.getEvaluationTree());
            logger.info("");
        });

        detectorEvaluator.registerPostExtractableCallback(detectorAggregateEvaluationResult -> {
            eventSystem.publishEvent(Event.PreparationsCompleted, detectorAggregateEvaluationResult.getEvaluationTree());
            logger.debug("Counting detectors that will be evaluated.");

            Integer extractionCount = detectorAggregateEvaluationResult.getExtractionCount();
            eventSystem.publishEvent(Event.ExtractionCount, extractionCount);
            eventSystem.publishEvent(Event.DiscoveryCount, extractionCount); //right now discovery and extraction are the same. -jp 8/14/19

            logger.debug("Total number of detectors: {}", extractionCount);
        });

        detectorEvaluator.registerPostDiscoveryCallback(detectorAggregateEvaluationResult -> {
            eventSystem.publishEvent(Event.DiscoveriesCompleted, detectorAggregateEvaluationResult.getEvaluationTree());
        });

        detectorEvaluator.registerPostExtractionCallback(detectorAggregateEvaluationResult -> {
            eventSystem.publishEvent(Event.ExtractionsCompleted, detectorAggregateEvaluationResult.getEvaluationTree());
        });

        DetectorAggregateEvaluationResult evaluationResult = detectorEvaluator.evaluate(rootEvaluation);

        logger.debug("Finished detectors.");
        Map<DetectorType, StatusType> statusMap = extractStatus(detectorEvaluations);
        publishStatusEvents(statusMap);
        publishFileEvents(detectorEvaluations);
        detectorIssuePublisher.publishEvents(eventSystem, rootEvaluation);
        publishMissingDetectorEvents(requiredDetectors, evaluationResult.getApplicableDetectorTypes());

        Map<CodeLocation, DetectCodeLocation> codeLocationMap = createCodeLocationMap(detectorEvaluations, directory);

        DetectorToolResult detectorToolResult = new DetectorToolResult(
            detectorNameVersionHandler.finalDecision().getChosenNameVersion().orElse(null),
            new ArrayList<>(codeLocationMap.values()),
            evaluationResult.getApplicableDetectorTypes(),
            new HashSet<>(),
            rootEvaluation,
            codeLocationMap
        );

        //Completed.
        logger.debug("Finished running detectors.");
        eventSystem.publishEvent(Event.DetectorsComplete, detectorToolResult);

        return detectorToolResult;
    }

    private DetectorNameVersionHandler createNameVersionHandler(String projectDetector) {
        Optional<DetectorType> preferredProjectDetector = Optional.empty();
        if (StringUtils.isNotBlank(projectDetector)) {
            preferredProjectDetector = preferredDetectorTypeFromString(projectDetector);
        }

        DetectorNameVersionHandler detectorNameVersionHandler;
        if (preferredProjectDetector.isPresent()) {
            detectorNameVersionHandler = new PreferredDetectorNameVersionHandler(preferredProjectDetector.get());
        } else {
            detectorNameVersionHandler = new DetectorNameVersionHandler(Collections.singletonList(DetectorType.GIT));
        }

        return detectorNameVersionHandler;
    }

    private Optional<DetectorType> preferredDetectorTypeFromString(String detectorTypeRaw) {
        String detectorType = detectorTypeRaw.trim().toUpperCase();
        if (StringUtils.isNotBlank(detectorType)) {
            if (DetectorType.getPossibleNames().contains(detectorType)) {
                return Optional.of(DetectorType.valueOf(detectorType));
            } else {
                logger.info("A valid preferred detector type was not provided, deciding project name automatically.");
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Map<DetectorType, StatusType> extractStatus(List<DetectorEvaluation> detectorEvaluations) {
        EnumMap<DetectorType, StatusType> statusMap = new EnumMap<>(DetectorType.class);
        for (DetectorEvaluation detectorEvaluation : detectorEvaluations) {
            DetectorType detectorType = detectorEvaluation.getDetectorType();
            Optional<StatusType> foundStatusType = determineDetectorExtractionStatus(detectorEvaluation);
            if (foundStatusType.isPresent()) {
                StatusType statusType = foundStatusType.get();
                if (statusType == StatusType.FAILURE || !statusMap.containsKey(detectorType)) {
                    statusMap.put(detectorType, statusType);
                }
            }
        }
        return statusMap;
    }

    private Optional<StatusType> determineDetectorExtractionStatus(DetectorEvaluation detectorEvaluation) {
        StatusType statusType = null;
        if (detectorEvaluation.isApplicable()) {
            if (detectorEvaluation.isExtractable()) {
                if (detectorEvaluation.wasExtractionSuccessful()) {
                    statusType = StatusType.SUCCESS;
                } else {
                    statusType = StatusType.FAILURE;

                    boolean extractionUnknownFailure = !detectorEvaluation.wasExtractionFailure() && !detectorEvaluation.wasExtractionException();
                    if (extractionUnknownFailure) {
                        logger.warn("An issue occurred in the detector system, an unknown evaluation status was created. Please contact support.");
                    }
                }
            } else if (detectorEvaluation.isFallbackExtractable() || detectorEvaluation.isPreviousExtractable()) {
                statusType = StatusType.SUCCESS;
            } else {
                statusType = StatusType.FAILURE;
            }
        }
        return Optional.ofNullable(statusType);
    }

    private Map<CodeLocation, DetectCodeLocation> createCodeLocationMap(List<DetectorEvaluation> detectorEvaluations, File directory) {
        return detectorEvaluations.stream()
                   .filter(DetectorEvaluation::wasExtractionSuccessful)
                   .map(it -> codeLocationConverter.toDetectCodeLocation(directory, it))
                   .map(Map::entrySet)
                   .flatMap(Collection::stream)
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void publishStatusEvents(Map<DetectorType, StatusType> statusMap) {
        statusMap.forEach((detectorType, statusType) -> eventSystem.publishEvent(Event.StatusSummary, new DetectorStatus(detectorType, statusType)));
        if (statusMap.containsValue(StatusType.FAILURE)) {
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_DETECTOR, "One or more detectors were not successful."));
        }
    }

    private void publishFileEvents(List<DetectorEvaluation> detectorEvaluations) {
        logger.debug("Publishing file events.");
        for (DetectorEvaluation detectorEvaluation : detectorEvaluations) {
            if (detectorEvaluation.getDetectable() != null) {
                for (File file : detectorEvaluation.getDetectable().getFoundRelevantFiles()) {
                    eventSystem.publishEvent(Event.CustomerFileOfInterest, file);
                }
            }
            if (detectorEvaluation.getExtraction() != null) {
                for (File file : detectorEvaluation.getExtraction().getRelevantFiles()) {
                    eventSystem.publishEvent(Event.CustomerFileOfInterest, file);
                }
                List<File> paths = detectorEvaluation.getExtraction().getUnrecognizedPaths();
                if (paths != null && !paths.isEmpty()) {
                    eventSystem.publishEvent(Event.UnrecognizedPaths, new UnrecognizedPaths(detectorEvaluation.getDetectorType().toString(), paths));
                }
            }
        }
    }

    private void publishMissingDetectorEvents(List<DetectorType> requiredDetectors, Set<DetectorType> applicable) {
        Set<DetectorType> missingDetectors = requiredDetectors.stream()
                                                 .filter(it -> !applicable.contains(it))
                                                 .collect(Collectors.toSet());
        if (!missingDetectors.isEmpty()) {
            String missingDetectorDisplay = missingDetectors.stream().map(Enum::toString).collect(Collectors.joining(","));
            logger.error("One or more required detector types were not found: {}", missingDetectorDisplay);
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_DETECTOR_REQUIRED));
        }
    }
}
