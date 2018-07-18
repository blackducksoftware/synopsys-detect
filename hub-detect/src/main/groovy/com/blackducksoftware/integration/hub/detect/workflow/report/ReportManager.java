package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling.BomToolProfiler;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionReporter;

public class ReportManager {
    // all entry points to reporting

    private final BomToolProfiler bomToolProfiler;
    private final PhoneHomeManager phoneHomeManager;
    private final DiagnosticManager diagnosticManager;

    private final ExtractionReporter extractionReporter;

    // Summary, print collections or final groups or information.
    private final SearchSummaryReporter searchSummaryReporter;
    private final PreparationSummaryReporter preparationSummaryReporter;
    private final ExtractionSummaryReporter extractionSummaryReporter;

    private final LogReportWriter logWriter = new LogReportWriter();

    public ReportManager(final BomToolProfiler bomToolProfiler, final PhoneHomeManager phoneHomeManager, final DiagnosticManager diagnosticManager, final ExtractionReporter extractionReporter,
            final PreparationSummaryReporter preparationSummaryReporter, final ExtractionSummaryReporter extractionSummaryReporter, final SearchSummaryReporter searchSummaryReporter) {
        this.bomToolProfiler = bomToolProfiler;
        this.phoneHomeManager = phoneHomeManager;
        this.diagnosticManager = diagnosticManager;
        this.extractionReporter = extractionReporter;
        this.preparationSummaryReporter = preparationSummaryReporter;
        this.extractionSummaryReporter = extractionSummaryReporter;
        this.searchSummaryReporter = searchSummaryReporter;
    }

    // Reports
    public void searchCompleted(final List<BomToolEvaluation> bomToolEvaluations) {
        searchSummaryReporter.print(logWriter, bomToolEvaluations);
    }

    public void preparationCompleted(final List<BomToolEvaluation> bomToolEvaluations) {
        preparationSummaryReporter.write(logWriter, bomToolEvaluations);
    }

    public void extractionsCompleted(final List<BomToolEvaluation> bomToolEvaluations) {
        phoneHomeManager.startPhoneHome(bomToolProfiler.getAggregateBomToolGroupTimes());
        diagnosticManager.completedBomToolEvaluations(bomToolEvaluations);
    }

    public void codeLocationsCompleted(final List<BomToolEvaluation> bomToolEvaluations, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        extractionSummaryReporter.writeSummary(logWriter, bomToolEvaluations, codeLocationNameMap);
        diagnosticManager.completedCodeLocations(bomToolEvaluations, codeLocationNameMap);
    }

    // Profiling
    public void applicableStarted(final BomTool bomTool) {
        bomToolProfiler.applicableStarted(bomTool);
    }

    public void applicableEnded(final BomTool bomTool) {
        bomToolProfiler.applicableEnded(bomTool);
    }

    public void extractableStarted(final BomTool bomTool) {
        bomToolProfiler.extractableStarted(bomTool);
    }

    public void extractableEnded(final BomTool bomTool) {
        bomToolProfiler.extractableEnded(bomTool);
    }

    public void extractionStarted(final BomToolEvaluation bomToolEvaluation, final ExtractionId extractionId) {
        diagnosticManager.startLoggingExtraction(extractionId);
        extractionReporter.startedExtraction(logWriter, bomToolEvaluation.getBomTool(), extractionId);
        bomToolProfiler.extractionStarted(bomToolEvaluation.getBomTool());
    }

    public void extractionEnded(final BomToolEvaluation bomToolEvaluation, final ExtractionId extractionId) {
        bomToolProfiler.extractionEnded(bomToolEvaluation.getBomTool());

        if (diagnosticManager.isDiagnosticModeOn()) {
            final List<File> diagnosticFiles = bomToolEvaluation.getBomTool().getRelevantDiagnosticFiles();
            for (final File file : diagnosticFiles) {
                diagnosticManager.registerFileOfInterest(extractionId, file);
            }
        }
        extractionReporter.endedExtraction(logWriter, bomToolEvaluation.getExtraction());
        diagnosticManager.stopLoggingExtraction(extractionId);
    }
}
