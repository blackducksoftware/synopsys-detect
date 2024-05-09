package com.synopsys.integration.detectable.detectable.result;

public class GivenFileNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "The given file (%s) was not found or not readable";

    public GivenFileNotFoundDetectableResult(String givenFilePath) {
        super(String.format(FORMAT, givenFilePath));
    }
}
