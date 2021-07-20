/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import java.util.Collection;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.result.DetectResult;

public class StatusEventPublisher {
    private EventSystem eventSystem;

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

    public void publishOperation(Operation detectOperation) {
        eventSystem.publishEvent(Event.DetectOperation, detectOperation);
    }

    public void publishOperationsComplete(Collection<Operation> detectOperations) {
        eventSystem.publishEvent(Event.DetectOperationsComplete, detectOperations);
    }
}
