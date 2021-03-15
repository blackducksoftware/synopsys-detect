/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.exception;

import com.synopsys.integration.exception.IntegrationException;

public class DetectableException extends IntegrationException {
    private static final long serialVersionUID = 1L;

    public DetectableException() {
        super();
    }

    public DetectableException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DetectableException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DetectableException(final String message) {
        super(message);
    }

    public DetectableException(final Throwable cause) {
        super(cause);
    }

}
