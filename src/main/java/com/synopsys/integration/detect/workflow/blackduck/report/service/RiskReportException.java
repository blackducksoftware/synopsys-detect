package com.synopsys.integration.detect.workflow.blackduck.report.service;

public class RiskReportException extends Exception {
    public RiskReportException() {
        super();
    }

    public RiskReportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RiskReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public RiskReportException(String message) {
        super(message);
    }

    public RiskReportException(Throwable cause) {
        super(cause);
    }

}
