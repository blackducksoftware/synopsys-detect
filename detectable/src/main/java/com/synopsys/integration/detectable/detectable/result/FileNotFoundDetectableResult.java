package com.synopsys.integration.detectable.detectable.result;

public class FileNotFoundDetectableResult extends FailedDetectableResult {
    public static final String PREFIX = "No file was found with pattern: ";

    public FileNotFoundDetectableResult(String pattern) {
        super(PREFIX, pattern);
    }
}
