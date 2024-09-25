package com.blackduck.integration.detectable.detectable.exception;

import com.blackduck.integration.exception.IntegrationException;

public class DetectableException extends IntegrationException {
    private static final long serialVersionUID = 1L;

    public DetectableException() {
        super();
    }

    public DetectableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DetectableException(String message, Throwable cause) {
        super(message, cause);
    }

    public DetectableException(String message) {
        super(message);
    }

    public DetectableException(Throwable cause) {
        super(cause);
    }

}
