package com.synopsys.integration.detector.result;

public class NotNestableBeneathDetectableDetectorResult extends FailedDetectorResult {
    public NotNestableBeneathDetectableDetectorResult(String detectableName) {
        super("Not nestable below extracted: " + detectableName);
    }
}
