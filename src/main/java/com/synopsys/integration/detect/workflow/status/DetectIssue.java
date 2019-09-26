package com.synopsys.integration.detect.workflow.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.event.EventType;

public class DetectIssue {
    public DetectIssueType getType() {
        return type;
    }

    public List<String> getMessages() {
        return messages;
    }

    private DetectIssueType type;
    private List<String> messages;

    public DetectIssue(final DetectIssueType type, List<String> messages) {
        this.type = type;
        this.messages = messages;
    }

    public static void publish(EventSystem eventSystem, DetectIssueType type, String... messages) {
        eventSystem.publishEvent(Event.Issue, new DetectIssue(type, Arrays.asList(messages)));
    }
}
