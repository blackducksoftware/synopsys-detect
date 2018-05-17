package com.blackducksoftware.integration.hub.detect.strategy.result;

public class InspectorNotFoundStrategyResult extends FailedStrategyResult {
    private final String inspectorName;

    public InspectorNotFoundStrategyResult(final String inspectorName) {
        this.inspectorName = inspectorName;
    }

    @Override
    public String toDescription() {
        return "No " + inspectorName + " inspector was found.";
    }
}
