package com.synopsys.integration.detectable.detectable.result;

public class NotNestableDetectableResult extends FailedDetectableResult {
    @Override
    public String toDescription() {
        return "Not nestable and a detector already applied in parent directory.";
    }
}
