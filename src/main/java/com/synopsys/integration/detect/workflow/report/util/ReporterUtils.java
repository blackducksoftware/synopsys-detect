/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.util;

import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

public class ReporterUtils {
    public static void printHeader(final ReportWriter writer, final String title) {
        writer.writeLine();
        writer.writeHeader();
        writer.writeLine(title);
        writer.writeHeader();
    }

    public static void printFooter(final ReportWriter writer) {
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine();
    }
}
