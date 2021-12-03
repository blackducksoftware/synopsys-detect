package com.synopsys.integration.detector.result;

public class NotSelfNestableDetectorResult extends FailedDetectorResult {
    public NotSelfNestableDetectorResult() {
        super("Nestable but this detector already applied in a parent directory.", NotSelfNestableDetectorResult.class);
    }
}
