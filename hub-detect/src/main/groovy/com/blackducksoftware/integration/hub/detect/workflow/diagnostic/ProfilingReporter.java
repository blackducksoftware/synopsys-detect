package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.util.List;

public class ProfilingReporter {

    public void writeReport(final DiagnosticReportWriter writer, final List<BomToolTime> timings) {

        for (final BomToolTime bomToolTime : timings) {
            writer.writeLine("\t" + padToLength(bomToolTime.getBomTool().getDescriptiveName(), 30) + "\t" + bomToolTime.getMs());
        }

    }

    private String padToLength(final String text, final int length) {
        String outText = text;
        while (outText.length() < length) {
            outText += " ";
        }
        return outText;
    }
}
