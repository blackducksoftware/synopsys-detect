package com.synopsys.integration.detect.workflow.report.util;

import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

public class ReporterUtils {
    public static void printHeader(ReportWriter writer, String title) {
        writer.writeLine();
        writer.writeHeader();
        writer.writeLine(title);
        writer.writeHeader();
    }

    public static void printFooter(ReportWriter writer) {
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine();
    }
}
