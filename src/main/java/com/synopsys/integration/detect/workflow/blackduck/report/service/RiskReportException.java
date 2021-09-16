/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.report.service;

public class RiskReportException extends Exception {
    public RiskReportException() {
        super();
    }

    public RiskReportException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RiskReportException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RiskReportException(final String message) {
        super(message);
    }

    public RiskReportException(final Throwable cause) {
        super(cause);
    }

}
