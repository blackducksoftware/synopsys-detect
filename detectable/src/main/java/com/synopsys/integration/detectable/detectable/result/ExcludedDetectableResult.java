package com.synopsys.integration.detectable.detectable.result;

public class ExcludedDetectableResult extends FailedDetectableResult {
    private static final String DEFAULT = "Detector type was excluded.";
    
    public ExcludedDetectableResult() {
        super(DEFAULT);
    }
}
