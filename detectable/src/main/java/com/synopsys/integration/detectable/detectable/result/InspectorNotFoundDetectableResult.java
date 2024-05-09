package com.synopsys.integration.detectable.detectable.result;

public class InspectorNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "No %s inspector was found.";

    public InspectorNotFoundDetectableResult(String inspectorName) {
        super(String.format(FORMAT, inspectorName));
    }
}
