package com.synopsys.integration.detect.workflow.status;

import java.util.Collection;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.result.DetectResult;

public class StatusEventPublisher {
    private final EventSystem eventSystem;

    public StatusEventPublisher(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    public void publishStatusSummary(Status status) {
        eventSystem.publishEvent(Event.StatusSummary, status);
    }

    public void publishIssue(DetectIssue issue) {
        eventSystem.publishEvent(Event.Issue, issue);
    }

    public void publishDetectResult(DetectResult detectResult) {
        eventSystem.publishEvent(Event.ResultProduced, detectResult);
    }

    public void publishOperationsComplete(Collection<Operation> detectOperations) {
        eventSystem.publishEvent(Event.DetectOperationsComplete, detectOperations);
    }
}
