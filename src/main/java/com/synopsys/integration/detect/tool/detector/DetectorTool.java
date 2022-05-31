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
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.status.DetectorStatus;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.accuracy.DetectableEvaluationResult;
import com.synopsys.integration.detector.accuracy.DetectableEvaluator;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorEvaluationOptions;
import com.synopsys.integration.detector.accuracy.DetectorEvaluator;
import com.synopsys.integration.detector.accuracy.DetectorExtract;
import com.synopsys.integration.detector.accuracy.DetectorRuleEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorSearch;
import com.synopsys.integration.detector.accuracy.EntryPointEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationUtil;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.finder.DirectoryFindResult;
import com.synopsys.integration.detector.finder.DirectoryFinder;
import com.synopsys.integration.detector.finder.DirectoryFinderOptions;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.util.NameVersion;

public class DetectorTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DirectoryFinder directoryFinder;
    private final ExtractionEnvironmentProvider extractionEnvironmentProvider;
    private final EventSystem eventSystem;
    private final CodeLocationConverter codeLocationConverter;
    private final DetectorIssuePublisher detectorIssuePublisher;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DetectorEventPublisher detectorEventPublisher;

    public DetectorTool(
        DirectoryFinder directoryFinder,
        ExtractionEnvironmentProvider extractionEnvironmentProvider,
        EventSystem eventSystem,
        CodeLocationConverter codeLocationConverter,
        DetectorIssuePublisher detectorIssuePublisher,
        StatusEventPublisher statusEventPublisher,
        ExitCodePublisher exitCodePublisher,
        DetectorEventPublisher detectorEventPublisher
    ) {
        this.directoryFinder = directoryFinder;
        this.extractionEnvironmentProvider = extractionEnvironmentProvider;
        this.eventSystem = eventSystem;
        this.codeLocationConverter = codeLocationConverter;
        this.detectorIssuePublisher = detectorIssuePublisher;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.detectorEventPublisher = detectorEventPublisher;
    }

    public DetectorToolResult performDetectors(
        File directory,
        DetectorRuleSet detectorRuleSet,
        DirectoryFinderOptions directoryFinderOptions,
        DetectorEvaluationOptions evaluationOptions,
        String projectDetector,
        List<DetectorType> requiredDetectors,
        FileFinder fileFinder
    ) {
        logger.debug("Starting detector file system traversal.");
        Optional<DirectoryFindResult> findResultOptional = directoryFinder.findDirectories(directory, directoryFinderOptions, fileFinder);

        if (!findResultOptional.isPresent()) {
            logger.error("The source directory could not be searched for detectors - detector tool failed.");
            logger.error("Please ensure the provided source path is a directory and detect has access.");
            exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_CONFIGURATION, "Detector tool failed to run on the configured source path.");
            return new DetectorToolResult();
        }

        DirectoryFindResult findResult = findResultOptional.get();

        DetectorSearch detectorSearch = new DetectorSearch();
        DetectorExtract detectorExtract = new DetectorExtract(new DetectableEvaluator());
        DetectorEvaluator detectorEvaluator = new DetectorEvaluator(detectorSearch, detectorExtract, evaluationOptions, extractionEnvironmentProvider::createExtractionEnvironment);

        DetectorEvaluation evaluation = detectorEvaluator.evaluate(findResult, detectorRuleSet);
        logger.debug("Finished detectors.");

        printExplanations(evaluation);
        List<DetectorRuleEvaluation> allFound = DetectorEvaluationUtil.allDescendentFound(evaluation);
        Map<DetectorType, StatusType> statusMap = extractStatus(allFound);
        publishStatusEvents(statusMap);
        publishFileEvents(allFound);

        detectorIssuePublisher.publishIssues(statusEventPublisher, allFound);
        Set<DetectorType> allFoundTypes = allFound.stream()
            .map(DetectorRuleEvaluation::getRule)
            .map(DetectorRule::getDetectorType)
            .collect(Collectors.toSet());
        publishMissingDetectorEvents(requiredDetectors, allFoundTypes);

        Map<CodeLocation, DetectCodeLocation> codeLocationMap = createCodeLocationMap(allFound, directory);

        DetectorEvaluationNameVersionDecider detectorEvaluationNameVersionDecider = new DetectorEvaluationNameVersionDecider(new DetectorNameVersionDecider());
        Optional<NameVersion> bomToolProjectNameVersion = detectorEvaluationNameVersionDecider.decideSuggestion(evaluation, projectDetector);
        logger.debug("Finished evaluating detectors for project info.");

        DetectorToolResult detectorToolResult = new DetectorToolResult(
            bomToolProjectNameVersion.orElse(null),
            new ArrayList<>(codeLocationMap.values()),
            allFoundTypes,
            new HashSet<>(),
            evaluation, //TODO: REmove?
            codeLocationMap
        );

        //Completed.
        logger.debug("Finished running detectors.");
        detectorEventPublisher.publishDetectorsComplete(detectorToolResult);

        return detectorToolResult;
    }

    private void printExplanations(DetectorEvaluation root) {
        logger.info(ReportConstants.HEADING);
        logger.info("Detector Report");
        logger.info(ReportConstants.HEADING);
        boolean anyFound = false;
        for (DetectorEvaluation detectorEvaluation : DetectorEvaluationUtil.asFlatList(root)) {
            List<DetectorRuleEvaluation> found = detectorEvaluation.getFoundDetectorRuleEvaluations();
            if (!found.isEmpty()) {
                anyFound = true;
                logger.info("\t" + detectorEvaluation.getDirectory() + " (depth " + detectorEvaluation.getDepth() + ")");

                found.forEach(ruleEvaluation -> {
                    EntryPointEvaluation selectedEntryPoint = ruleEvaluation.getSelectedEntryPointEvaluation();
                    for (DetectableEvaluationResult detectable : selectedEntryPoint.getEvaluatedDetectables()) {
                        boolean isTheExtracted = selectedEntryPoint.getExtractedEvaluation()
                            .map(detectable::equals)
                            .orElse(false);

                        String detectableStatus;
                        if (isTheExtracted) {
                            if (detectable.wasExtractionSuccessful()) {
                                detectableStatus = "SUCCESS";
                            } else {
                                detectableStatus = "FAILED";
                            }
                        } else {
                            detectableStatus = "SKIPPED";
                        }
                        logger.info("\t\t" + detectable.getDetectableDefinition().getName() + ": " + detectableStatus);
                        detectable.getExplanations().forEach(explanation -> {
                            logger.info("\t\t\t" + explanation.describeSelf());
                        });
                        if (isTheExtracted) {
                            break;
                        }
                    }
                });
            }
        }
        if (!anyFound) {
            logger.info("No detectors found.");
        }
        logger.info(ReportConstants.RUN_SEPARATOR);
    }

    private Map<DetectorType, StatusType> extractStatus(List<DetectorRuleEvaluation> detectorEvaluations) {
        EnumMap<DetectorType, StatusType> statusMap = new EnumMap<>(DetectorType.class);
        for (DetectorRuleEvaluation detectorEvaluation : detectorEvaluations) {
            DetectorType detectorType = detectorEvaluation.getRule().getDetectorType();
            StatusType statusType = determineDetectorExtractionStatus(detectorEvaluation);
            if (statusType == StatusType.FAILURE || !statusMap.containsKey(detectorType)) {
                statusMap.put(detectorType, statusType);
            }
        }
        return statusMap;
    }

    //This assumes the DetectorRuleEvaluation was found. (
    private StatusType determineDetectorExtractionStatus(DetectorRuleEvaluation detectorEvaluation) {
        EntryPointEvaluation selectedEntryPointEvaluation = detectorEvaluation.getSelectedEntryPointEvaluation();
        Optional<DetectableEvaluationResult> extractedEvaluation = selectedEntryPointEvaluation.getExtractedEvaluation();
        if (extractedEvaluation.map(DetectableEvaluationResult::wasExtractionSuccessful).orElse(false)) {
            return StatusType.SUCCESS;
        }
        return StatusType.FAILURE;
    }

    private Map<CodeLocation, DetectCodeLocation> createCodeLocationMap(List<DetectorRuleEvaluation> foundDetectorEvaluations, File directory) {
        return foundDetectorEvaluations.stream()
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

    private void publishFileEvents(List<DetectorRuleEvaluation> foundDetectors) {
        logger.debug("Publishing file events.");
        for (DetectorRuleEvaluation detectorEvaluation : foundDetectors) {
            Optional<DetectableEvaluationResult> extractedEvaluationOptional = detectorEvaluation.getSelectedEntryPointEvaluation().getExtractedEvaluation();
            if (!extractedEvaluationOptional.isPresent())
                continue;
            DetectableEvaluationResult extractedEvaluation = extractedEvaluationOptional.get();
            if (extractedEvaluation.getExplanations() != null) {
                for (File file : extractedEvaluation.getRelevantFiles()) {
                    detectorEventPublisher.publishCustomerFileOfInterest(file);
                }
            }
            if (detectorEvaluation.getExtraction().isPresent()) {
                Extraction extraction = detectorEvaluation.getExtraction().get();
                for (File file : extraction.getRelevantFiles()) {
                    detectorEventPublisher.publishCustomerFileOfInterest(file);
                }
                List<File> paths = extraction.getUnrecognizedPaths();
                if (paths != null && !paths.isEmpty()) {
                    detectorEventPublisher.publishUnrecognizedPaths(new UnrecognizedPaths(detectorEvaluation.getRule().getDetectorType().toString(), paths));
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
