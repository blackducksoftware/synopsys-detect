/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.result;

public class ReportDetectResult implements DetectResult {
    private final String reportName;
    private final String filePath;

    public ReportDetectResult(final String reportName, final String filePath) {
        this.reportName = reportName;
        this.filePath = filePath;
    }

    @Override
    public String getResultLocation() {
        return filePath;
    }

    @Override
    public String getResultMessage() {
        return String.format("%s: %s", reportName, filePath);
    }
}
