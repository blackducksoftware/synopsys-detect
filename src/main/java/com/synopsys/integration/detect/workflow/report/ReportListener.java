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

public class ReportListener {
    // all entry points to reporting
    private final EventSystem eventSystem;

    // Summary, print collections or final groups or information.
    private final SearchSummaryReporter searchSummaryReporter;
    private final ExtractionSummaryReporter extractionSummaryReporter;

    private final ReportWriter traceLogWriter = new TraceLogReportWriter();
    private final ReportWriter debugLogWriter = new DebugLogReportWriter();

    public static ReportListener createDefault(EventSystem eventSystem) {
        return new ReportListener(
            eventSystem,
            new ExtractionSummaryReporter(),
            new SearchSummaryReporter(),
            new DetectorIssuePublisher()
        );
    }

    public ReportListener(
        EventSystem eventSystem,
        ExtractionSummaryReporter extractionSummaryReporter,
        SearchSummaryReporter searchSummaryReporter,
        DetectorIssuePublisher detectorIssuePublisher
    ) {
        this.eventSystem = eventSystem;
        this.extractionSummaryReporter = extractionSummaryReporter;
        this.searchSummaryReporter = searchSummaryReporter;

        eventSystem.registerListener(Event.DetectorsComplete, this::bomToolsComplete);

        eventSystem.registerListener(Event.DetectCodeLocationNamesCalculated, event -> codeLocationsCompleted(event.getCodeLocationNames()));
    }

    private DetectorToolResult detectorToolResult;

    public void bomToolsComplete(DetectorToolResult detectorToolResult) {
        this.detectorToolResult = detectorToolResult;

        searchSummaryReporter.print(debugLogWriter, detectorToolResult.getDetectorReports());
        DetailedSearchSummaryReporter detailedSearchSummaryReporter = new DetailedSearchSummaryReporter();
        detailedSearchSummaryReporter.print(traceLogWriter, detectorToolResult.getDetectorReports());

    }

    public void codeLocationsCompleted(Map<DetectCodeLocation, String> codeLocationNameMap) {
        if (detectorToolResult != null) {
            extractionSummaryReporter.writeSummary(
                debugLogWriter,
                detectorToolResult.getDetectorReports(),
                detectorToolResult.getCodeLocationMap(),
                codeLocationNameMap,
                false
            );
        }
    }
}
