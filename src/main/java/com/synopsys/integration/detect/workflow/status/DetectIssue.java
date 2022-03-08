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

    public String getTitle() {
        return title;
    }

    private final DetectIssueType type;
    private final List<String> messages;
    private final String title;

    public DetectIssue(DetectIssueType type, String title, List<String> messages) {
        this.type = type;
        this.title = title;
        this.messages = messages;
    }

    public static void publish(EventSystem eventSystem, DetectIssueType type, String title, String... messages) {
        publish(eventSystem, type, title, Arrays.asList(messages));
    }

    public static void publish(EventSystem eventSystem, DetectIssueType type, String title, List<String> messages) {
        eventSystem.publishEvent(Event.Issue, new DetectIssue(type, title, messages));
    }
}
