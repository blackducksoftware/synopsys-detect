package com.synopsys.integration.detectable.detectable.result;

public class ExceptionDetectableResult extends FailedDetectableResult {
    private static final String PREFIX = "Exception occurred: ";

    public ExceptionDetectableResult(Exception exception) {
        super(PREFIX, exception.getMessage());
    }
}
