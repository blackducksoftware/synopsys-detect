package com.blackducksoftware.integration.hub.detect.event;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.PreparationResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.shutdown.ExitCodeRequest;
import com.blackducksoftware.integration.hub.detect.workflow.status.Status;

public enum Event {
    SearchCompleted(SearchResult.class),
    PreparationCompleted(PreparationResult.class),
    ExtractionCompleted(ExtractionResult.class),
    ApplicableStarted(BomTool.class),
    ApplicableEnded(BomTool.class),
    ExtractableStarted(BomTool.class),
    ExtractableEnded(BomTool.class),
    ExtractionStarted(BomToolEvaluation.class),
    ExtractionEnded(BomToolEvaluation.class),
    ExitCode(ExitCodeRequest.class),
    StatusSummary(Status.class),
    OutputFileOfInterest(File.class),
    CustomerFileOfInterest(File.class);

    Event(Class clazz) {
        this.eventClass = clazz;
    }

    private Class eventClass;

    public Class getEventClass() {
        return eventClass;
    }

}
