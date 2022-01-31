package com.synopsys.integration.detectable.detectable.result;

public class FileNotFoundDetectableResult extends FailedDetectableResult {
    private final String pattern;

    public FileNotFoundDetectableResult(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toDescription() {
        return "No file was found with pattern: " + pattern;
    }
}
