package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling.BomToolProfiler;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling.BomToolTime;

public class ProfilingReporter {

    public void writeReport(final ReportWriter writer, final BomToolProfiler bomToolProfiler) {
        writer.writeSeperator();
        writer.writeLine("Applicable Times");
        writer.writeSeperator();
        writeAggregateReport(writer, bomToolProfiler.getApplicableTimings());
        writer.writeSeperator();
        writer.writeLine("Extractable Times");
        writer.writeSeperator();
        writeReport(writer, bomToolProfiler.getExtractableTimings());
        writer.writeSeperator();
        writer.writeLine("Extraction Times");
        writer.writeSeperator();
        writeReport(writer, bomToolProfiler.getExtractionTimings());
    }

    private void writeAggregateReport(final ReportWriter writer, final List<BomToolTime> timings) {
        final Map<String, Long> aggregated = new HashMap<>();

        for (final BomToolTime bomToolTime : timings) {
            final String name = bomToolTime.getBomTool().getDescriptiveName();
            if (!aggregated.containsKey(name)) {
                aggregated.put(name, 0L);
            }
            aggregated.put(name, aggregated.get(name) + bomToolTime.getMs());
        }

        for (final String key : aggregated.keySet()) {
            writer.writeLine("\t" + padToLength(key, 30) + "\t" + aggregated.get(key));
        }
    }

    private void writeReport(final ReportWriter writer, final List<BomToolTime> timings) {

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
