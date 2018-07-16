package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import com.blackducksoftware.integration.hub.detect.testutils.ObjectPrinter;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticReportManager.ReportTypes;

public class DiagnosticProfilingManager {

    private final DiagnosticReportManager diagnosticReportManager;
    private final BomToolProfiler bomToolProfiler;

    public DiagnosticProfilingManager(final DiagnosticReportManager diagnosticReportManager, final BomToolProfiler bomToolProfiler) {
        this.diagnosticReportManager = diagnosticReportManager;
        this.bomToolProfiler = bomToolProfiler;
    }

    public void finish() {
        final DiagnosticReportWriter profileWriter = diagnosticReportManager.getReportWriter(ReportTypes.BOM_TOOL_PROFILE);

        final ProfilingReporter reporter = new ProfilingReporter();
        profileWriter.writeSeperator();
        profileWriter.writeLine("Applicable Times");
        profileWriter.writeSeperator();
        reporter.writeReport(profileWriter, bomToolProfiler.getApplicableTimings());
        profileWriter.writeSeperator();
        profileWriter.writeLine("Extractable Times");
        profileWriter.writeSeperator();
        reporter.writeReport(profileWriter, bomToolProfiler.getExtractableTimings());
        profileWriter.writeSeperator();
        profileWriter.writeLine("Extraction Times");
        profileWriter.writeSeperator();
        reporter.writeReport(profileWriter, bomToolProfiler.getExtractionTimings());

        final DiagnosticReportWriter extractionReport = diagnosticReportManager.getReportWriter(ReportTypes.EXTRACTION_STATE);
        for (final BomToolTime time : bomToolProfiler.getExtractionTimings()) {
            extractionReport.writeSeperator();
            extractionReport.writeLine(time.getBomTool().getDescriptiveName());
            ObjectPrinter.printObjectPrivate(extractionReport, null, time.getBomTool());
            extractionReport.writeSeperator();
        }
    }
}
