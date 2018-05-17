package com.blackducksoftware.integration.hub.detect.extraction.result;

public class ExceptionStrategyResult extends FailedStrategyResult {
    private final Exception exception;

    public ExceptionStrategyResult(final Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toDescription() {
        return "Exception occured: " + exception.getMessage();
    }
}
