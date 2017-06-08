package com.blackducksoftware.integration.hub.detect.exception;

public class DetectException extends Exception {
    public DetectException() {
        super();
    }

    public DetectException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DetectException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DetectException(final String message) {
        super(message);
    }

    public DetectException(final Throwable cause) {
        super(cause);
    }

}
