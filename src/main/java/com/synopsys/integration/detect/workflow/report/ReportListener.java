/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report;

import java.util.Map;

import com.synopsys.integration.detect.tool.detector.DetectorIssuePublisher;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.report.writer.DebugLogReportWriter;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detect.workflow.report.writer.TraceLogReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class ReportListener {
    // all entry points to reporting
    private final EventSystem eventSystem;

    // Summary, print collections or final groups or information.
    private final SearchSummaryReporter searchSummaryReporter;
    private final PreparationSummaryReporter preparationSummaryReporter;
    private final ExtractionSummaryReporter extractionSummaryReporter;

    private final ReportWriter traceLogWriter = new TraceLogReportWriter();
    private final ReportWriter debugLogWriter = new DebugLogReportWriter();
    private final ExtractionLogger extractionLogger;

    public static ReportListener createDefault(EventSystem eventSystem) {
        return new ReportListener(eventSystem, new PreparationSummaryReporter(), new ExtractionSummaryReporter(), new SearchSummaryReporter(), new DetectorIssuePublisher(), new ExtractionLogger());
    }

    public ReportListener(EventSystem eventSystem,
        PreparationSummaryReporter preparationSummaryReporter, ExtractionSummaryReporter extractionSummaryReporter, SearchSummaryReporter searchSummaryReporter, DetectorIssuePublisher detectorIssuePublisher,
        ExtractionLogger extractionLogger) {
        this.eventSystem = eventSystem;
        this.preparationSummaryReporter = preparationSummaryReporter;
        this.extractionSummaryReporter = extractionSummaryReporter;
        this.searchSummaryReporter = searchSummaryReporter;
        this.extractionLogger = extractionLogger;

        eventSystem.registerListener(Event.SearchCompleted, this::searchCompleted);
        eventSystem.registerListener(Event.PreparationsCompleted, this::preparationsCompleted);
        eventSystem.registerListener(Event.DetectorsComplete, this::bomToolsComplete);

        eventSystem.registerListener(Event.DetectCodeLocationNamesCalculated, event -> codeLocationsCompleted(event.getCodeLocationNames()));

        eventSystem.registerListener(Event.ExtractionCount, this::extractionCount);
        eventSystem.registerListener(Event.ExtractionStarted, this::extractionStarted);
        eventSystem.registerListener(Event.ExtractionEnded, this::extractionEnded);

    }

    // Reports
    public void searchCompleted(DetectorEvaluationTree rootEvaluation) {
        searchSummaryReporter.print(debugLogWriter, rootEvaluation);
        DetailedSearchSummaryReporter detailedSearchSummaryReporter = new DetailedSearchSummaryReporter();
        detailedSearchSummaryReporter.print(traceLogWriter, rootEvaluation);
    }

    public void preparationsCompleted(DetectorEvaluationTree detectorEvaluationTree) {
        preparationSummaryReporter.write(debugLogWriter, detectorEvaluationTree);
    }

    public void extractionCount(Integer count) {
        extractionLogger.setExtractionCount(count);
    }

    public void extractionStarted(DetectorEvaluation detectorEvaluation) {
        extractionLogger.extractionStarted(detectorEvaluation);
    }

    public void extractionEnded(DetectorEvaluation detectorEvaluation) {
        extractionLogger.extractionEnded(detectorEvaluation);
    }

    private DetectorToolResult detectorToolResult;

    public void bomToolsComplete(DetectorToolResult detectorToolResult) {
        this.detectorToolResult = detectorToolResult;
    }

    public void codeLocationsCompleted(Map<DetectCodeLocation, String> codeLocationNameMap) {
        if (detectorToolResult != null && detectorToolResult.getRootDetectorEvaluationTree().isPresent()) {
            extractionSummaryReporter.writeSummary(debugLogWriter, detectorToolResult.getRootDetectorEvaluationTree().get(), detectorToolResult.getCodeLocationMap(), codeLocationNameMap, false);
        }
    }
}
