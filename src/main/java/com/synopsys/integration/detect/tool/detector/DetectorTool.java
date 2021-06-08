/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.nameversion.DetectorEvaluationNameVersionDecider;
import com.synopsys.integration.detect.workflow.nameversion.DetectorNameVersionDecider;
import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.status.DetectorStatus;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.evaluation.DetectorAggregateEvaluationResult;
import com.synopsys.integration.detector.evaluation.DetectorEvaluationOptions;
import com.synopsys.integration.detector.evaluation.DetectorEvaluator;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.util.NameVersion;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectorFinder detectorFinder;
    private final ExtractionEnvironmentProvider extractionEnvironmentProvider;
    private final EventSystem eventSystem;
    private final CodeLocationConverter codeLocationConverter;
    private final DetectorIssuePublisher detectorIssuePublisher;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DetectorEventPublisher detectorEventPublisher;

    public DetectorTool(DetectorFinder detectorFinder, ExtractionEnvironmentProvider extractionEnvironmentProvider, EventSystem eventSystem, CodeLocationConverter codeLocationConverter,
        DetectorIssuePublisher detectorIssuePublisher, StatusEventPublisher statusEventPublisher, ExitCodePublisher exitCodePublisher, DetectorEventPublisher detectorEventPublisher) {
        this.detectorFinder = detectorFinder;
        this.extractionEnvironmentProvider = extractionEnvironmentProvider;
        this.eventSystem = eventSystem;
        this.codeLocationConverter = codeLocationConverter;
        this.detectorIssuePublisher = detectorIssuePublisher;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.detectorEventPublisher = detectorEventPublisher;
    }

    public DetectorToolResult performDetectors(File directory, DetectorRuleSet detectorRuleSet, DetectorFinderOptions detectorFinderOptions, DetectorEvaluationOptions evaluationOptions, String projectDetector,
        List<DetectorType> requiredDetectors, FileFinder fileFinder) {
        logger.debug("Initializing detector system.");
        Optional<DetectorEvaluationTree> possibleRootEvaluation;

        logger.debug("Starting detector file system traversal.");
        possibleRootEvaluation = detectorFinder.findDetectors(directory, detectorRuleSet, detectorFinderOptions, fileFinder);

        if (!possibleRootEvaluation.isPresent()) {
            logger.error("The source directory could not be searched for detectors - detector tool failed.");
            logger.error("Please ensure the provided source path is a directory and detect has access.");
            exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_CONFIGURATION, "Detector tool failed to run on the configured source path.");
            return new DetectorToolResult();
        }

        DetectorEvaluationTree rootEvaluation = possibleRootEvaluation.get();
        List<DetectorEvaluation> detectorEvaluations = rootEvaluation.allDescendentEvaluations();

        logger.trace("Setting up detector events.");
        //DetectorNameVersionHandler detectorNameVersionHandler = createNameVersionHandler(projectDetector);
        DetectorEvaluatorBroadcaster eventBroadcaster = new DetectorEvaluatorBroadcaster(eventSystem);

        DetectorEvaluator detectorEvaluator = new DetectorEvaluator(evaluationOptions, extractionEnvironmentProvider::createExtractionEnvironment);
        detectorEvaluator.setDetectorEvaluatorListener(eventBroadcaster);

        detectorEvaluator.registerPostApplicableCallback(detectorAggregateEvaluationResult -> {
            detectorEventPublisher.publishApplicableCompleted(detectorAggregateEvaluationResult.getApplicableDetectorTypes());
            detectorEventPublisher.publishSearchCompleted(detectorAggregateEvaluationResult.getEvaluationTree());
            logger.info("");
        });

        detectorEvaluator.registerPostExtractableCallback(detectorAggregateEvaluationResult -> {
            detectorEventPublisher.publishPreparationsCompleted(detectorAggregateEvaluationResult.getEvaluationTree());
            logger.debug("Counting detectors that will be evaluated.");

            Integer extractionCount = detectorAggregateEvaluationResult.getExtractionCount();
            detectorEventPublisher.publishExtractionCount(extractionCount);

            logger.debug("Total number of detectors: {}", extractionCount);
        });

        detectorEvaluator.registerPostExtractionCallback(detectorAggregateEvaluationResult -> detectorEventPublisher.publishExtractionsCompleted(detectorAggregateEvaluationResult.getEvaluationTree()));

        DetectorAggregateEvaluationResult evaluationResult = detectorEvaluator.evaluate(rootEvaluation);

        logger.debug("Finished detectors."); // TODO- finished extractions?

        printExplanations(rootEvaluation);

        Map<DetectorType, StatusType> statusMap = extractStatus(detectorEvaluations);
        publishStatusEvents(statusMap);
        publishFileEvents(detectorEvaluations);
        detectorIssuePublisher.publishEvents(statusEventPublisher, rootEvaluation);
        publishMissingDetectorEvents(requiredDetectors, evaluationResult.getApplicableDetectorTypes());

        Map<CodeLocation, DetectCodeLocation> codeLocationMap = createCodeLocationMap(detectorEvaluations, directory);

        DetectorEvaluationNameVersionDecider detectorEvaluationNameVersionDecider = new DetectorEvaluationNameVersionDecider(new DetectorNameVersionDecider());
        Optional<NameVersion> bomToolProjectNameVersion = detectorEvaluationNameVersionDecider.decideSuggestion(detectorEvaluations, projectDetector);
        logger.debug("Finished evaluating detectors for project info.");

        DetectorToolResult detectorToolResult = new DetectorToolResult(
            bomToolProjectNameVersion.orElse(null),
            new ArrayList<>(codeLocationMap.values()),
            evaluationResult.getApplicableDetectorTypes(),
            new HashSet<>(),
            rootEvaluation,
            codeLocationMap
        );

        //Completed.
        logger.debug("Finished running detectors.");
        detectorEventPublisher.publishDetectorsComplete(detectorToolResult);

        return detectorToolResult;
    }

    private void printExplanations(DetectorEvaluationTree root) {
        logger.info(ReportConstants.HEADING);
        logger.info("Detector Report");
        logger.info(ReportConstants.HEADING);
        boolean anyFound = false;
        for (DetectorEvaluationTree tree : root.asFlatList()) {
            List<DetectorEvaluation> applicable = DetectorEvaluationUtils.applicableChildren(tree);
            if (!applicable.isEmpty()) {
                anyFound = true;
                logger.info("\t" + tree.getDirectory() + " (depth " + tree.getDepthFromRoot() + ")");
                applicable.forEach(evaluation -> {
                    logger.info("\t\t" + evaluation.getDetectorRule().getDescriptiveName());
                    evaluation.getAllExplanations().forEach(explanation ->
                                                                logger.info("\t\t\t" + explanation.describeSelf()));
                });
            }
        }
        if (!anyFound) {
            logger.info("No detectors found.");
        }
        logger.info(ReportConstants.RUN_SEPARATOR);
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
        statusMap.forEach((detectorType, statusType) ->
                              statusEventPublisher.publishStatusSummary(new DetectorStatus(detectorType, statusType)));
        if (statusMap.containsValue(StatusType.FAILURE)) {
            exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_DETECTOR, "One or more detectors were not successful.");
        }
    }

    private void publishFileEvents(List<DetectorEvaluation> detectorEvaluations) {
        logger.debug("Publishing file events.");
        for (DetectorEvaluation detectorEvaluation : detectorEvaluations) {
            if (detectorEvaluation.getDetectable() != null) {
                for (File file : detectorEvaluation.getAllRelevantFiles()) {
                    detectorEventPublisher.publishCustomerFileOfInterest(file);
                }
            }
            if (detectorEvaluation.getExtraction() != null) {
                for (File file : detectorEvaluation.getExtraction().getRelevantFiles()) {
                    detectorEventPublisher.publishCustomerFileOfInterest(file);
                }
                List<File> paths = detectorEvaluation.getExtraction().getUnrecognizedPaths();
                if (paths != null && !paths.isEmpty()) {
                    detectorEventPublisher.publishUnrecognizedPaths(new UnrecognizedPaths(detectorEvaluation.getDetectorType().toString(), paths));
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
            exitCodePublisher.publishExitCode(new ExitCodeRequest(ExitCodeType.FAILURE_DETECTOR_REQUIRED));
        }
    }
}
