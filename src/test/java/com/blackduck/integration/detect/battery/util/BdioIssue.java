package com.blackduck.integration.detect.battery.util;

public class BdioIssue {
    private final String issue;

    BdioIssue(String issue) {
        this.issue = issue;
    }

    public String getIssue() {
        return issue;
    }
}
