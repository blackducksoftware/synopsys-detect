package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.ExcludeIncludeEnumFilter;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.tool.detector.report.rule.EvaluatedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.rule.ExtractedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.util.DetectorReporter;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.nameversion.DetectorEvaluationNameVersionDecider;
import com.synopsys.integration.detect.workflow.nameversion.DetectorNameVersionDecider;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.DetectorStatus;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.accuracy.DetectableEvaluator;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorEvaluationOptions;
import com.synopsys.integration.detector.accuracy.DetectorEvaluator;
import com.synopsys.integration.detector.accuracy.DetectorExtract;
import com.synopsys.integration.detector.accuracy.DetectorSearch;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.finder.DirectoryFindResult;
import com.synopsys.integration.detector.finder.DirectoryFinder;
import com.synopsys.integration.detector.finder.DirectoryFinderOptions;
import com.synopsys.integration.detector.rule.DetectableDefinition;
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
        ExcludeIncludeEnumFilter<DetectorType> requiredAccuracyTypes,
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

        List<DetectorDirectoryReport> reports = new DetectorReporter().generateReport(evaluation);
        DetectorToolResult toolResult = publishAllResults(reports, directory, projectDetector, requiredDetectors, requiredAccuracyTypes);

        //Completed.
        logger.debug("Finished running detectors.");
        detectorEventPublisher.publishDetectorsComplete(toolResult);

        return toolResult;
    }

    private DetectorToolResult publishAllResults(
        List<DetectorDirectoryReport> reports,
        File directory,
        String projectDetector,
        List<DetectorType> requiredDetectors,
        ExcludeIncludeEnumFilter<DetectorType> requiredAccuracyTypes
    ) {

        printExplanations(reports);
        Map<DetectorType, StatusType> statusMap = extractStatus(reports);
        publishStatusEvents(statusMap);
        publishFileEvents(reports);

        detectorIssuePublisher.publishIssues(statusEventPublisher, reports);
        checkForAccuracy(reports, requiredAccuracyTypes);

        Set<DetectorType> allFoundTypes = statusMap.keySet();
        publishMissingDetectorEvents(requiredDetectors, allFoundTypes);

        Map<CodeLocation, DetectCodeLocation> codeLocationMap = createCodeLocationMap(reports, directory);

        DetectorEvaluationNameVersionDecider detectorEvaluationNameVersionDecider = new DetectorEvaluationNameVersionDecider(new DetectorNameVersionDecider());
        Optional<NameVersion> bomToolProjectNameVersion = detectorEvaluationNameVersionDecider.decideSuggestion(reports, projectDetector);

        logger.debug("Finished evaluating detectors for project info.");

        return new DetectorToolResult(
            bomToolProjectNameVersion.orElse(null),
            new ArrayList<>(codeLocationMap.values()),
            allFoundTypes,
            new HashSet<>(),
            reports,
            codeLocationMap
        );
    }

    private void printExplanations(List<DetectorDirectoryReport> reports) {
        logger.info(ReportConstants.HEADING);
        logger.info("Detector Report");
        logger.info(ReportConstants.HEADING);
        boolean anyFound = false;
        for (DetectorDirectoryReport report : reports) {
            if (report.anyFound()) {
                anyFound = true;
                logger.info("\t" + report.getDirectory() + " (depth " + report.getDepth() + ")");
                report.getExtractedDetectors().forEach(extracted -> {
                    logger.info("\t\t" + extracted.getExtractedDetectable().getDetectable().getName() + ": SUCCESS");
                    extracted.getExtractedDetectable().getExplanations().forEach(explanation -> {
                        logger.info("\t\t\t" + explanation.describeSelf());
                    });

                    extracted.getAttemptedDetectables().forEach(attempted -> {
                        logger.info("\t\t" + attempted.getDetectable().getName() + ": ATTEMPTED");
                        logger.info("\t\t\t" + attempted.getStatusReason());
                        attempted.getExplanations().forEach(explanation -> {
                            logger.info("\t\t\t" + explanation.describeSelf());
                        });
                    });
                });
                report.getNotExtractedDetectors().forEach(notExtracted -> {
                    notExtracted.getAttemptedDetectables().forEach(attempted -> {
                        logger.info("\t\t" + attempted.getDetectable().getName() + ": FAILED");
                        attempted.getExplanations().forEach(explanation -> {
                            logger.info("\t\t\t" + explanation.describeSelf());
                        });
                    });
                });
            }
        }
        if (!anyFound) {
            logger.info("No detectors found.");
        }
        logger.info(ReportConstants.RUN_SEPARATOR);
    }

    private Map<DetectorType, StatusType> extractStatus(List<DetectorDirectoryReport> reports) {
        Set<DetectorType> success = reports.stream()
            .flatMap(directory -> directory.getExtractedDetectors().stream())
            .map(ExtractedDetectorRuleReport::getRule)
            .map(DetectorRule::getDetectorType)
            .collect(Collectors.toSet());

        Set<DetectorType> failure = reports.stream()
            .flatMap(directory -> directory.getNotExtractedDetectors().stream())
            .map(EvaluatedDetectorRuleReport::getRule)
            .map(DetectorRule::getDetectorType)
            .collect(Collectors.toSet());

        EnumMap<DetectorType, StatusType> statusMap = new EnumMap<>(DetectorType.class);

        success.forEach(detector -> statusMap.put(detector, StatusType.SUCCESS));
        failure.forEach(detector -> statusMap.put(detector, StatusType.FAILURE));

        return statusMap;
    }

    private Map<CodeLocation, DetectCodeLocation> createCodeLocationMap(List<DetectorDirectoryReport> reports, File sourcePath) {
        Map<CodeLocation, DetectCodeLocation> codeLocations = new HashMap<>();
        reports.forEach(report -> report.getExtractedDetectors().stream()
            .map(extracted -> codeLocationConverter.toDetectCodeLocation(
                sourcePath,
                extracted.getExtractedDetectable().getExtraction(),
                report.getDirectory(),
                extracted.getRule().getDetectorType().toString()
            ))
            .forEach(codeLocations::putAll));

        return codeLocations;
    }

    private void publishStatusEvents(Map<DetectorType, StatusType> statusMap) {
        statusMap.forEach((detectorType, statusType) ->
            statusEventPublisher.publishStatusSummary(new DetectorStatus(detectorType, statusType)));
        if (statusMap.containsValue(StatusType.FAILURE)) {
            exitCodePublisher.publishExitCode(ExitCodeType.FAILURE_DETECTOR, "One or more detectors were not successful.");
        }
    }

    private void publishFileEvents(List<DetectorDirectoryReport> reports) { //TODO (detectors): What to publish here? Only succesfull? For now publishing only extracted.
        logger.debug("Publishing file events.");
        reports.stream()
            .flatMap(directory -> directory.getExtractedDetectors().stream())
            .forEach(extracted -> {
                extracted.getExtractedDetectable().getRelevantFiles().forEach(detectorEventPublisher::publishCustomerFileOfInterest);
                extracted.getExtractedDetectable().getExtraction().getRelevantFiles()
                    .forEach(detectorEventPublisher::publishCustomerFileOfInterest); //TODO (detectors): Is it weird i have to seperately publish extraction?

                List<File> paths = extracted.getExtractedDetectable().getExtraction().getUnrecognizedPaths();
                detectorEventPublisher.publishUnrecognizedPaths(new UnrecognizedPaths(extracted.getRule().getDetectorType().toString(), paths));

                extracted.getAttemptedDetectables().forEach(attempted -> {
                    attempted.getRelevantFiles().forEach(detectorEventPublisher::publishCustomerFileOfInterest);
                });
            });

    }

    private void checkForAccuracy(List<DetectorDirectoryReport> reports, ExcludeIncludeEnumFilter<DetectorType> requiredAccuracyTypes) {
        //TODO (detectors): Should this check not-extracted too? Here is the potential for remediation, was there an attempted one that met accuracy?
        for (DetectorDirectoryReport report : reports) {
            report.getExtractedDetectors().forEach(extracted -> {
                if (requiredAccuracyTypes.shouldInclude(extracted.getRule().getDetectorType())) {
                    DetectableDefinition extractedDetectable = extracted.getExtractedDetectable().getDetectable();
                    if (extractedDetectable.getAccuracyType() != DetectableAccuracyType.HIGH) {
                        //Accuracy not met!
                        exitCodePublisher.publishExitCode(new ExitCodeRequest(ExitCodeType.FAILURE_ACCURACY_NOT_MET));
                        List<String> messages = new ArrayList<>();

                        messages.add("Accuracy Not Met: " + extracted.getRule().getDetectorType());
                        messages.add("\tExtraction for " + extractedDetectable.getName() + " has accuracy of " + extractedDetectable.getAccuracyType() + " but HIGH is required.");
                        statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.DETECTOR, "Detector Issue", messages));
                    }
                }
            });
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
