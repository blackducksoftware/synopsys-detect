package com.blackducksoftware.integration.hub.detect.workflow.report;

public class ErrorSummaryBomToolError {
    private final String bomToolName;
    private final String reason;

    public ErrorSummaryBomToolError(final String bomToolName, final String reason) {
        this.bomToolName = bomToolName;
        this.reason = reason;
    }

    public String getBomToolName() {
        return bomToolName;
    }

    public String getReason() {
        return reason;
    }
}
