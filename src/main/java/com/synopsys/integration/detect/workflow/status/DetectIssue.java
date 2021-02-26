/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import java.util.Arrays;
import java.util.List;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class DetectIssue {
    public DetectIssueType getType() {
        return type;
    }

    public List<String> getMessages() {
        return messages;
    }

    private final DetectIssueType type;
    private final List<String> messages;

    public DetectIssue(final DetectIssueType type, final List<String> messages) {
        this.type = type;
        this.messages = messages;
    }

    public static void publish(final EventSystem eventSystem, final DetectIssueType type, final String... messages) {
        eventSystem.publishEvent(Event.Issue, new DetectIssue(type, Arrays.asList(messages)));
    }
}
