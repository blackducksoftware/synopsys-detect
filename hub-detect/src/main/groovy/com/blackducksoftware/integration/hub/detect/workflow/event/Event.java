package com.blackducksoftware.integration.hub.detect.workflow.event;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeRequest;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolsResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.status.Status;

public class Event {
    public static EventType<SearchResult> SearchCompleted = new EventType(SearchResult.class);
    public static EventType<PreparationResult> PreparationsCompleted = new EventType(PreparationResult.class);
    public static EventType<ExtractionResult> ExtractionsCompleted = new EventType(ExtractionResult.class);
    public static EventType<BomToolsResult> BomToolsComplete = new EventType(BomToolsResult.class);
    public static EventType<BomTool> ApplicableStarted = new EventType(BomTool.class);
    public static EventType<BomTool> ApplicableEnded = new EventType(BomTool.class);
    public static EventType<BomTool> ExtractableStarted = new EventType(BomTool.class);
    public static EventType<BomTool> ExtractableEnded = new EventType(BomTool.class);
    public static EventType<BomToolEvaluation> ExtractionStarted = new EventType(BomToolEvaluation.class);
    public static EventType<BomToolEvaluation> ExtractionEnded = new EventType(BomToolEvaluation.class);
    public static EventType<DetectCodeLocationResult> CodeLocationsCalculated = new EventType(DetectCodeLocationResult.class);
    public static EventType<ExitCodeRequest> ExitCode = new EventType(ExitCodeRequest.class);
    public static EventType<Status> StatusSummary = new EventType(Status.class);
    public static EventType<File> OutputFileOfInterest = new EventType(File.class);
    public static EventType<File> CustomerFileOfInterest = new EventType(File.class);
}
