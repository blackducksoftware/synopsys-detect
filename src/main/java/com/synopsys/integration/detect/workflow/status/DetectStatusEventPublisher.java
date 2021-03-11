/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.result.DetectResult;

public class DetectStatusEventPublisher implements StatusEventPublisher {
    private EventSystem eventSystem;

    public DetectStatusEventPublisher(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    @Override
    public void publishStatusSummary(Status status) {
        eventSystem.publishEvent(Event.StatusSummary, status);
    }

    @Override
    public void publishIssue(DetectIssue issue) {
        eventSystem.publishEvent(Event.Issue, issue);
    }

    @Override
    public void publishDetectResult(DetectResult detectResult) {
        eventSystem.publishEvent(Event.ResultProduced, detectResult);
    }
}
