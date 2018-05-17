package com.blackducksoftware.integration.hub.detect.extraction.result;

public class NotNestableStrategyResult extends FailedStrategyResult {
    @Override
    public String toDescription() {
        return "Not nestable and already applied in parent directory.";
    }
}
