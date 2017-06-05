package com.blackducksoftware.integration.hub.boss.exception;

public class BossException extends Exception {
    public BossException() {
        super();
    }

    public BossException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BossException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BossException(final String message) {
        super(message);
    }

    public BossException(final Throwable cause) {
        super(cause);
    }

}
