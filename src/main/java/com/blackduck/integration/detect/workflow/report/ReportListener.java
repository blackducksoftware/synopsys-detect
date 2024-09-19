package com.blackduck.integration.detect.workflow.report;

import com.blackduck.integration.detect.tool.detector.DetectorToolResult;
import com.blackduck.integration.detect.workflow.event.Event;
import com.blackduck.integration.detect.workflow.event.EventSystem;
import com.blackduck.integration.detect.workflow.report.writer.DebugLogReportWriter;
import com.blackduck.integration.detect.workflow.report.writer.ReportWriter;
import com.blackduck.integration.detect.workflow.report.writer.TraceLogReportWriter;

public class ReportListener {

    // Summary, print collections or final groups or information.
    private final SearchSummaryReporter searchSummaryReporter;

    private final ReportWriter traceLogWriter = new TraceLogReportWriter();
    private final ReportWriter debugLogWriter = new DebugLogReportWriter();

    public static ReportListener createDefault(EventSystem eventSystem) {
        return new ReportListener(
            eventSystem,
            new SearchSummaryReporter()
        );
    }

    public ReportListener(
        EventSystem eventSystem,
        SearchSummaryReporter searchSummaryReporter
    ) {
        this.searchSummaryReporter = searchSummaryReporter;

        eventSystem.registerListener(Event.DetectorsComplete, this::bomToolsComplete);
    }

    public void bomToolsComplete(DetectorToolResult detectorToolResult) {
        searchSummaryReporter.print(debugLogWriter, detectorToolResult.getDetectorReports());
        DetailedSearchSummaryReporter detailedSearchSummaryReporter = new DetailedSearchSummaryReporter();
        detailedSearchSummaryReporter.print(traceLogWriter, detectorToolResult.getDetectorReports());

    }
}
