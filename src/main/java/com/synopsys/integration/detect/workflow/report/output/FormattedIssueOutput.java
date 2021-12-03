package com.synopsys.integration.detect.workflow.report.output;

import java.util.List;

public class FormattedIssueOutput {
    public final String type;
    public final String title;
    public final List<String> messages;

    public FormattedIssueOutput(String type, String title, List<String> messages) {
        this.type = type;
        this.title = title;
        this.messages = messages;
    }
}
