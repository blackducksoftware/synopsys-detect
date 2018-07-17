package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report.BomToolStateReporter;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report.ProfilingReporter;

public class DiagnosticReportManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<ReportTypes, DiagnosticReportWriter> reportWriters = new HashMap<>();

    public enum ReportTypes {
        SEARCH("search_report", "Search Result Report", "A breakdown of bom tool searching by directory."),
        EXTRACTION_STATE("extraction_state_report", "Bom Tool Extraction State Report", "All fields and state of any extractabe bom tool post extraction."),
        APPLICABLE_STATE("applicable_state_report", "Bom Tool Applicable State Report", "All fields and state of any applicable bom tool post extraction."),
        BOM_TOOL("bom_tool_report", "Bom Tool Report", "A breakdown of bom tool's that were applicable and their preparation and extraction results."),
        BOM_TOOL_PROFILE("bom_tool_profile_report", "Bom Tool Profile Report", "A breakdown of timing and profiling for all bom tools."),
        CODE_LOCATIONS("code_location_report", "Code Location Report", "A breakdown of code locations created, their dependencies and status results.");

        String reportFileName;
        String reportTitle;
        String reportDescription;

        ReportTypes(final String reportFileName, final String reportTitle, final String reportDescription) {
            this.reportFileName = reportFileName;
            this.reportTitle = reportTitle;
            this.reportDescription = reportDescription;
        }

        String getReportFileName() {
            return reportFileName;
        }

        String getReportTitle() {
            return reportTitle;
        }

        String getReportDescription() {
            return reportDescription;
        }
    }

    private File reportDirectory;
    private String runId;
    private final BomToolProfiler bomToolProfiler;

    public DiagnosticReportManager(final BomToolProfiler bomToolProfiler) {
        this.bomToolProfiler = bomToolProfiler;
    }

    public void init(final File reportDirectory, final String runId) {
        this.reportDirectory = reportDirectory;
        this.runId = runId;
        createReports();
    }

    public void finish() {
        writeReports();

        closeReportWriters();
    }

    public void completedBomToolEvaluations(final List<BomToolEvaluation> bomToolEvaluations) {
        final BomToolStateReporter stateReporter = new BomToolStateReporter();
        stateReporter.writeExtractionStateReport(getReportWriter(ReportTypes.EXTRACTION_STATE), bomToolEvaluations);
        stateReporter.writeApplicableStateReport(getReportWriter(ReportTypes.APPLICABLE_STATE), bomToolEvaluations);
    }

    private void writeReports() {
        final DiagnosticReportWriter profileWriter = getReportWriter(ReportTypes.BOM_TOOL_PROFILE);
        final ProfilingReporter reporter = new ProfilingReporter();
        reporter.writeReport(profileWriter, bomToolProfiler);
    }

    private void createReports() {
        for (final ReportTypes reportType : ReportTypes.values()) {
            createReportWriter(reportType);
        }
    }

    private DiagnosticReportWriter createReportWriter(final ReportTypes type) {
        try {
            final File reportFile = new File(reportDirectory, type.getReportFileName() + ".txt");
            final DiagnosticReportWriter diagnosticReportWriter = new DiagnosticReportWriter(reportFile, type.getReportTitle(), type.getReportDescription(), runId);
            reportWriters.put(type, diagnosticReportWriter);
            logger.info("Created report file: " + reportFile.getPath());
            return diagnosticReportWriter;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DiagnosticReportWriter getReportWriter(final ReportTypes type) {
        if (reportWriters.containsKey(type)) {
            return reportWriters.get(type);
        } else {
            return createReportWriter(type);
        }
    }

    private void closeReportWriters() {
        for (final DiagnosticReportWriter writer : reportWriters.values()) {
            writer.finish();
        }
    }
}
