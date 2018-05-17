package com.blackducksoftware.integration.hub.detect.strategy.result;

public class FileNotFoundStrategyResult extends FailedStrategyResult {
    private final String pattern;

    public FileNotFoundStrategyResult(final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toDescription() {
        return "No file was found with pattern: " + pattern;
    }
}
