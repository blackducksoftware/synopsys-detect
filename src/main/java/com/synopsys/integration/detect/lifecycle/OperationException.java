package com.synopsys.integration.detect.lifecycle;

public class OperationException extends Exception {
    private static final long serialVersionUID = 1L;

    private final Exception exception;

    public OperationException(Exception exception) {
        super(exception);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }
}
