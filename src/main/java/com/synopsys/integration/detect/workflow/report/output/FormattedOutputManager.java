package com.synopsys.integration.detect.workflow.report.output;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.detect.workflow.status.OperationType;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.DetectorStatusUtil;
import com.synopsys.integration.detector.accuracy.DetectableEvaluationResult;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorRuleEvaluation;
import com.synopsys.integration.detector.accuracy.EntryPointEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationUtil;
import com.synopsys.integration.detector.base.DetectorStatusType;
import com.synopsys.integration.util.NameVersion;

public class FormattedOutputManager {
    private final Set<String> codeLocations = new HashSet<>();
    private final List<Status> statusSummaries = new ArrayList<>();
    private final List<DetectResult> detectResults = new ArrayList<>();
    private final List<DetectIssue> detectIssues = new ArrayList<>();
    private final Map<String, List<File>> unrecognizedPaths = new HashMap<>();
    private final List<Operation> detectOperations = new LinkedList<>();
    private DetectorToolResult detectorToolResult = null;
    private NameVersion projectNameVersion = null;
    private SortedMap<String, String> rawMaskedPropertyValues = null;

    public FormattedOutputManager(EventSystem eventSystem) {
        eventSystem.registerListener(Event.DetectorsComplete, result -> detectorToolResult = result);
        eventSystem.registerListener(Event.StatusSummary, statusSummaries::add);
        eventSystem.registerListener(Event.Issue, detectIssues::add);
        eventSystem.registerListener(Event.ResultProduced, detectResults::add);
        eventSystem.registerListener(Event.CodeLocationsCompleted, codeLocations::addAll);
        eventSystem.registerListener(Event.UnrecognizedPaths, this::addUnrecognizedPaths);
        eventSystem.registerListener(Event.ProjectNameVersionChosen, nameVersion -> projectNameVersion = nameVersion);
        eventSystem.registerListener(Event.RawMaskedPropertyValuesCollected, keyValueMap -> rawMaskedPropertyValues = keyValueMap);
        eventSystem.registerListener(Event.DetectOperationsComplete, detectOperations::addAll);
    }

    public FormattedOutput createFormattedOutput(DetectInfo detectInfo) {
        FormattedOutput formattedOutput = new FormattedOutput();
        formattedOutput.formatVersion = "0.5.0";
        formattedOutput.detectVersion = detectInfo.getDetectVersion();

        formattedOutput.results = Bds.of(detectResults)
            .map(result -> new FormattedResultOutput(result.getResultLocation(), result.getResultMessage(), removeTabsFromMessages(result.getResultSubMessages())))
            .toList();

        formattedOutput.status = Bds.of(statusSummaries)
            .map(status -> new FormattedStatusOutput(status.getDescriptionKey(), status.getStatusType().toString()))
            .toList();

        formattedOutput.issues = Bds.of(detectIssues)
            .map(issue -> new FormattedIssueOutput(issue.getType().name(), issue.getTitle(), issue.getMessages()))
            .toList();
        formattedOutput.operations = visibleOperations();

        if (detectorToolResult != null) { //TODO (Detector): Add formatted output results...
            formattedOutput.detectors = convertDetectors();
        }
        if (projectNameVersion != null) {
            formattedOutput.projectName = projectNameVersion.getName();
            formattedOutput.projectVersion = projectNameVersion.getVersion();
        }

        formattedOutput.codeLocations = Bds.of(this.codeLocations)
            .map(FormattedCodeLocationOutput::new)
            .toList();

        formattedOutput.unrecognizedPaths = new HashMap<>();
        unrecognizedPaths.keySet().forEach(key -> formattedOutput.unrecognizedPaths.put(key, unrecognizedPaths.get(key).stream().map(File::toString).collect(Collectors.toList())));

        formattedOutput.propertyValues = rawMaskedPropertyValues;

        return formattedOutput;
    }

    private List<FormattedOperationOutput> visibleOperations() {
        return Bds.of(detectOperations)
            .filter(operation -> operation.getOperationType() == OperationType.PUBLIC
                || operation.getStatusType() != StatusType.SUCCESS) //EITHER a public operation or a failed internal operation
            .map(operation -> new FormattedOperationOutput(
                Operation.formatTimestamp(operation.getStartTime()),
                Operation.formatTimestamp(operation.getEndTime().orElse(null)),
                operation.getName(),
                operation.getStatusType().name()
            ))
            .toList();
    }

    private List<String> removeTabsFromMessages(List<String> messages) {
        if (messages.isEmpty()) {
            return messages;
        }
        // if a line starts with a tab character remove it.  Any other tabs replace it with spaces to preserve a similar look to the messages as the console output.
        return messages.stream()
            .filter(StringUtils::isNotBlank)
            .map(message -> StringUtils.replaceOnce(message, "\t", ""))
            .map(message -> StringUtils.replace(message, "\t", "  "))
            .collect(Collectors.toList());
    }

    private List<FormattedDetectorOutput> convertDetectors() {
        List<FormattedDetectorOutput> outputs = new ArrayList<>();
        if (detectorToolResult != null && detectorToolResult.getRootDetectorEvaluation().isPresent()) {
            for (DetectorEvaluation detectorEvaluation : DetectorEvaluationUtil.asFlatList(detectorToolResult.getRootDetectorEvaluation().get())) {
                List<DetectorRuleEvaluation> found = detectorEvaluation.getFoundDetectorRuleEvaluations();
                found.stream()
                    .map(this::convertFoundDetector)
                    .forEach(outputs::addAll);
            }
        }
        return outputs;
    }

    private List<FormattedDetectorOutput> convertFoundDetector(DetectorRuleEvaluation ruleEvaluation) {

        List<FormattedDetectorOutput> detectorOutputs = new ArrayList<>();

        EntryPointEvaluation selectedEntryPoint = ruleEvaluation.getSelectedEntryPointEvaluation();
        for (DetectableEvaluationResult detectable : selectedEntryPoint.getEvaluatedDetectables()) {
            boolean isTheExtracted = selectedEntryPoint.getExtractedEvaluation()
                .map(detectable::equals)
                .orElse(false);

            DetectorStatusType detectorStatus;
            if (isTheExtracted && detectable.wasExtractionSuccessful()) {
                detectorStatus = DetectorStatusType.SUCCESS;
            } else if (isTheExtracted) {
                detectorStatus = DetectorStatusType.FAILURE;
            } else {
                detectorStatus = DetectorStatusType.ATTEMPTED;
            }

            FormattedDetectorOutput detectorOutput = new FormattedDetectorOutput();
            detectorOutput.folder = ruleEvaluation.getEnvironment().getDirectory().toString();
            detectorOutput.detectorName = detectable.getDetectableDefinition().getName();
            ;
            detectorOutput.detectorType = ruleEvaluation.getRule().getDetectorType().toString();

            detectorOutput.extracted = detectable.wasExtractionSuccessful();
            detectorOutput.status = detectorStatus.toString(); //TODO (detector): This is tricky...
            detectorOutput.statusCode = DetectorStatusUtil.getStatusCode(detectable);
            detectorOutput.statusReason = DetectorStatusUtil.getStatusReason(detectable).toString();
            detectorOutput.explanations = Bds.of(detectable.getAllExplanations()).map(Explanation::describeSelf).toList();

            if (isTheExtracted) {
                Extraction extraction = selectedEntryPoint.getExtractedEvaluation().map(DetectableEvaluationResult::getExtraction).orElse(null);
                if (extraction == null)
                    continue;

                detectorOutput.extractedReason = extraction.getDescription();
                detectorOutput.relevantFiles = Bds.of(extraction.getRelevantFiles()).map(File::toString).toList();
                detectorOutput.projectName = extraction.getProjectName();
                detectorOutput.projectVersion = extraction.getProjectVersion();
                if (extraction.getCodeLocations() != null) {
                    detectorOutput.codeLocationCount = extraction.getCodeLocations().size();
                }
            }
            detectorOutputs.add(detectorOutput);

            if (isTheExtracted)
                break; //Only add up to the extracted, that is all ATTEMPTED and the final EXTRACTED
        }
        return detectorOutputs;
    }

    private void addUnrecognizedPaths(UnrecognizedPaths unrecognizedPaths) {
        if (!this.unrecognizedPaths.containsKey(unrecognizedPaths.getGroup())) {
            this.unrecognizedPaths.put(unrecognizedPaths.getGroup(), new ArrayList<>());
        }
        this.unrecognizedPaths.get(unrecognizedPaths.getGroup()).addAll(unrecognizedPaths.getPaths());
    }

}
