package com.synopsys.integration.detectable.detectable.result;

public class NotSelfNestableDetectableResult extends FailedDetectableResult {
    @Override
    public String toDescription() {
        return "Nestable but this detector already applied in a parent directory.";
    }
}
