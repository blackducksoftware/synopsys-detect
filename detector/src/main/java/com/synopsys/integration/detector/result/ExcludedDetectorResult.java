package com.synopsys.integration.detector.result;

public class ExcludedDetectorResult extends FailedDetectorResult {
    public ExcludedDetectorResult() {
        super("Detector type was excluded.");
    }
}
