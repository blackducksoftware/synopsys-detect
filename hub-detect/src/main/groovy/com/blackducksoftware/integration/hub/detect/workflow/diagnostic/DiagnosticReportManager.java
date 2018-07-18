package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling.BomToolProfiler;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report.BomToolStateReporter;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report.CodeLocationReporter;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report.OverviewReporter;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report.ProfilingReporter;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report.SearchReporter;
import com.blackducksoftware.integration.hub.detect.workflow.report.FileReportWriter;
import com.blackducksoftware.integration.hub.detect.workflow.report.LogReportWriter;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportWriter;

public class DiagnosticReportManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<ReportTypes, FileReportWriter> reportWriters = new HashMap<>();

    public enum ReportTypes {
        SEARCH("search_report", "Search Result Report", "A breakdown of bom tool searching by directory."),
        EXTRACTION_STATE("extraction_state_report", "Bom Tool Extraction State Report", "All fields and state of any extractabe bom tool post extraction."),
        APPLICABLE_STATE("applicable_state_report", "Bom Tool Applicable State Report", "All fields and state of any applicable bom tool post extraction."),
        BOM_TOOL("bom_tool_report", "Bom Tool Report", "A breakdown of bom tool's that were applicable and their preparation and extraction results."),
        BOM_TOOL_PROFILE("bom_tool_profile_report", "Bom Tool Profile Report", "A breakdown of timing and profiling for all bom tools."),
        CODE_LOCATIONS("code_location_report", "Code Location Report", "A breakdown of code locations created, their dependencies and status results."),
        DEPENDENCY_COUNTS("dependency_counts_report", "Dependency Count Report", "A breakdown of how many dependencies each bom tool group generated in their graphs.");

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
        try {
            final BomToolStateReporter stateReporter = new BomToolStateReporter();
            stateReporter.writeExtractionStateReport(getReportWriter(ReportTypes.EXTRACTION_STATE), bomToolEvaluations);
            stateReporter.writeApplicableStateReport(getReportWriter(ReportTypes.APPLICABLE_STATE), bomToolEvaluations);
        } catch (final Exception e) {
            logger.error("Failed to write bom tool state report.");
            e.printStackTrace();
        }

        try {
            final SearchReporter searchReporter = new SearchReporter();
            searchReporter.writeReport(getReportWriter(ReportTypes.SEARCH), bomToolEvaluations);
        } catch (final Exception e) {
            logger.error("Failed to write search report.");
            e.printStackTrace();
        }

        try {
            final OverviewReporter overviewReporter = new OverviewReporter();
            overviewReporter.writeReport(getReportWriter(ReportTypes.BOM_TOOL), bomToolEvaluations);
        } catch (final Exception e) {
            logger.error("Failed to write bom tool report.");
            e.printStackTrace();
        }
    }

    public void completedCodeLocations(final List<BomToolEvaluation> bomToolEvaluations, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        try {
            final ReportWriter clWriter = getReportWriter(ReportTypes.CODE_LOCATIONS);
            final ReportWriter dcWriter = getReportWriter(ReportTypes.DEPENDENCY_COUNTS);
            final CodeLocationReporter clReporter = new CodeLocationReporter();
            clReporter.writeCodeLocationReport(clWriter, dcWriter, bomToolEvaluations, codeLocationNameMap);
        } catch (final Exception e) {
            logger.error("Failed to write code location report.");
            e.printStackTrace();
        }
    }

    private void writeReports() {
        try {
            final ReportWriter profileWriter = getReportWriter(ReportTypes.BOM_TOOL_PROFILE);
            final ProfilingReporter reporter = new ProfilingReporter();
            reporter.writeReport(profileWriter, bomToolProfiler);
        } catch (final Exception e) {
            logger.error("Failed to write profiling report.");
            e.printStackTrace();
        }
    }

    private void createReports() {
        for (final ReportTypes reportType : ReportTypes.values()) {
            try {
                createReportWriter(reportType);
            } catch (final Exception e) {
                logger.error("Failed to create report: " + reportType.toString());
                e.printStackTrace();
            }
        }
    }

    private ReportWriter createReportWriter(final ReportTypes reportType) {
        try {
            final File reportFile = new File(reportDirectory, reportType.getReportFileName() + ".txt");
            final FileReportWriter fileReportWriter = new FileReportWriter(reportFile, reportType.getReportTitle(), reportType.getReportDescription(), runId);
            reportWriters.put(reportType, fileReportWriter);
            logger.info("Created report file: " + reportFile.getPath());
            return fileReportWriter;
        } catch (final Exception e) {
            logger.error("Failed to create report writer: " + reportType.toString());
            e.printStackTrace();
        }
        return new LogReportWriter();
    }

    public ReportWriter getReportWriter(final ReportTypes type) {
        if (reportWriters.containsKey(type)) {
            return reportWriters.get(type);
        } else {
            return createReportWriter(type);
        }
    }

    private void closeReportWriters() {
        for (final FileReportWriter writer : reportWriters.values()) {
            writer.finish();
        }
    }
}
