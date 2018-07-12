package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagnosticReportManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<ReportTypes, DiagnosticReportWriter> reportWriters = new HashMap<>();

    public enum ReportTypes {
        SEARCH("search_report"),
        EXTRACTION("extraction_report"),
        PREPARATION("preparation_report"),
        APPLICABLE_PROFILE("applicable_report"),
        CODE_LOCATIONS("code_location_report");

        String thing;

        ReportTypes(final String thing) {
            this.thing = thing;
        }

        String getReportFileName() {
            return thing;
        }
    }

    private File reportDirectory;
    private String runId;

    public void init(final File reportDirectory, final String runId) {
        this.reportDirectory = reportDirectory;
        this.runId = runId;
        createReports();
    }

    public void finish() {
        closeReportWriters();
    }

    public void cleanup() {
        deleteReportFiles();
    }

    private void createReports() {
        for (final ReportTypes reportType : ReportTypes.values()) {
            createReportWriter(reportType);
        }
    }

    private DiagnosticReportWriter createReportWriter(final ReportTypes type) {
        try {
            final File reportFile = new File(reportDirectory, type.getReportFileName() + ".txt");
            final DiagnosticReportWriter diagnosticReportWriter = new DiagnosticReportWriter(reportFile, type.toString(), runId);
            reportWriters.put(type, diagnosticReportWriter);
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

    private void deleteReportFiles() {
        for (final File file : reportDirectory.listFiles()) {
            try {
                file.delete();
            } catch (final SecurityException e) {
                logger.error("Failed to cleanup: " + file.getPath());
                e.printStackTrace();
            }
        }
    }
}
