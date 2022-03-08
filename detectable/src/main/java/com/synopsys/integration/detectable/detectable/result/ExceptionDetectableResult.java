package com.synopsys.integration.detectable.detectable.result;

public class ExceptionDetectableResult extends FailedDetectableResult {
    private final Exception exception;

    public ExceptionDetectableResult(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toDescription() {
        return "Exception occurred: " + exception.getMessage();
    }
}
