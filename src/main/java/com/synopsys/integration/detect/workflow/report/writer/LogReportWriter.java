/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.writer;

import com.synopsys.integration.detect.workflow.report.util.ReportConstants;

public abstract class LogReportWriter implements ReportWriter {
    @Override
    public void writeSeparator() {
        writeLine(ReportConstants.SEPARATOR);
    }

    @Override
    public void writeLine() {
        writeLine("");
    }

    @Override
    public void writeHeader() {
        writeLine(ReportConstants.HEADING);
    }

    @Override
    public void finish() {
        // Nothing to clean up
    }
}
