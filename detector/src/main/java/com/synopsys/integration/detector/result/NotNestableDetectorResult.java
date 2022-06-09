package com.synopsys.integration.detector.result;

public class NotNestableDetectorResult extends FailedDetectorResult {
    public NotNestableDetectorResult() {
        super("Not nestable and a detector already applied in parent directory.");
    }
}
