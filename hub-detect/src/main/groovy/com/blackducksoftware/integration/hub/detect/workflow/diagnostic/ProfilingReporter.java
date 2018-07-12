package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.util.List;

public class ProfilingReporter {

    public void writeReport(final DiagnosticReportWriter writer, final BomToolProfiler bomToolProfiler) {

        final List<BomToolTime> timings = bomToolProfiler.getApplicableTimings();

        for (final BomToolTime bomToolTime : timings) {
            writer.writeLine(bomToolTime.getBomTool().getDescriptiveName() + "\t\t\t\t" + bomToolTime.getMs());
        }

    }
}
