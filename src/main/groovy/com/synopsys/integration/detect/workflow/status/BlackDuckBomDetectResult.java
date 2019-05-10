package com.synopsys.integration.detect.workflow.status;

public class BlackDuckBomDetectResult implements DetectResult {
    private final String projectComponentsLink;

    public BlackDuckBomDetectResult(final String projectComponentsLink) {
        this.projectComponentsLink = projectComponentsLink;
    }

    @Override
    public String getResultMessage() {
        return String.format("Black Duck Project BOM: %s", projectComponentsLink);
    }
}
