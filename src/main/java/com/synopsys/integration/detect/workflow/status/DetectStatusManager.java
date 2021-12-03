package com.synopsys.integration.detect.workflow.status;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.log.IntLogger;

public class DetectStatusManager {
    private final List<Status> statusSummaries = new ArrayList<>();
    private final List<DetectResult> detectResults = new ArrayList<>();
    private final List<DetectIssue> detectIssues = new ArrayList<>();
    private final List<Operation> detectOperations = new LinkedList<>();

    public DetectStatusManager(EventSystem eventSystem) {
        eventSystem.registerListener(Event.StatusSummary, this::addStatusSummary);
        eventSystem.registerListener(Event.Issue, this::addIssue);
        eventSystem.registerListener(Event.ResultProduced, this::addDetectResult);
        eventSystem.registerListener(Event.DetectOperationsComplete, detectOperations::addAll);
    }

    public void addStatusSummary(Status status) {
        statusSummaries.add(status);
    }

    public void addIssue(DetectIssue issue) {
        detectIssues.add(issue);
    }

    public void addDetectResult(DetectResult detectResult) {
        detectResults.add(detectResult);
    }

    public void logDetectResults(IntLogger logger, ExitCodeType exitCodeType) {
        new DetectStatusLogger().logDetectStatus(logger, statusSummaries, detectResults, detectIssues, detectOperations, exitCodeType);
    }

    public boolean hasAnyFailure() {
        return statusSummaries.stream()
            .anyMatch(it -> it.getStatusType() == StatusType.FAILURE);
    }
}
