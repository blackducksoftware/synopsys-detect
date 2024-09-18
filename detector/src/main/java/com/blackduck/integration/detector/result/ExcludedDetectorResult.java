package com.blackduck.integration.detector.result;

public class ExcludedDetectorResult extends FailedDetectorResult {
    public ExcludedDetectorResult() {
        super("Detector type was excluded.");
    }
}
