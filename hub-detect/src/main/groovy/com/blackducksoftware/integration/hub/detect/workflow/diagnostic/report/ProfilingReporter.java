package com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling.BomToolProfiler;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling.BomToolTime;

public class ProfilingReporter {

    public void writeReport(final DiagnosticReportWriter writer, final BomToolProfiler bomToolProfiler) {
        final ProfilingReporter reporter = new ProfilingReporter();
        writer.writeSeperator();
        writer.writeLine("Applicable Times");
        writer.writeSeperator();
        writeReport(writer, bomToolProfiler.getApplicableTimings());
        writer.writeSeperator();
        writer.writeLine("Extractable Times");
        writer.writeSeperator();
        writeReport(writer, bomToolProfiler.getExtractableTimings());
        writer.writeSeperator();
        writer.writeLine("Extraction Times");
        writer.writeSeperator();
        writeReport(writer, bomToolProfiler.getExtractionTimings());
    }

    private void writeReport(final DiagnosticReportWriter writer, final List<BomToolTime> timings) {

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
