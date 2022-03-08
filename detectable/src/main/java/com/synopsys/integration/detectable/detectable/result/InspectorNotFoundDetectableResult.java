package com.synopsys.integration.detectable.detectable.result;

public class InspectorNotFoundDetectableResult extends FailedDetectableResult {
    private final String inspectorName;

    public InspectorNotFoundDetectableResult(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    @Override
    public String toDescription() {
        return "No " + inspectorName + " inspector was found.";
    }
}
