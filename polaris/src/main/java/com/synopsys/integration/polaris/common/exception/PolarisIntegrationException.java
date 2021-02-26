/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.exception;

import com.synopsys.integration.exception.IntegrationException;

public class PolarisIntegrationException extends IntegrationException {
    public PolarisIntegrationException() {
    }

    public PolarisIntegrationException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PolarisIntegrationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PolarisIntegrationException(final String message) {
        super(message);
    }

    public PolarisIntegrationException(final Throwable cause) {
        super(cause);
    }

}
