package com.blackducksoftware.integration.hub.detect.strategy.result;

public class NotNestableStrategyResult extends FailedStrategyResult {
    @Override
    public String toDescription() {
        return "Not nestable and already applied in parent directory.";
    }
}
