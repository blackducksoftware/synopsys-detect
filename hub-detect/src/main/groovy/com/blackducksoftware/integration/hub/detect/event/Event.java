package com.blackducksoftware.integration.hub.detect.event;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;

public enum Event {
    ApplicableStarted(BomTool.class),
    ApplicableEnded(BomTool.class),
    ExtractableStarted(BomTool.class),
    ExtractableEnded(BomTool.class),
    ExtractionStarted(BomToolEvaluation.class),
    ExtractionEnded(BomToolEvaluation.class);

    Event(Class clazz) {
        this.eventClass = clazz;
    }

    private Class eventClass;

    public Class getEventClass() {
        return eventClass;
    }

}
