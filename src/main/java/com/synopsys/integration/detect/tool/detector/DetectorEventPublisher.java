package com.synopsys.integration.detect.tool.detector;

import java.io.File;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;

public class DetectorEventPublisher {
    private final EventSystem eventSystem;

    public DetectorEventPublisher(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    public void publishCustomerFileOfInterest(File file) {
        eventSystem.publishEvent(Event.CustomerFileOfInterest, file);
    }

    public void publishDetectorsComplete(DetectorToolResult detectorToolResult) {
        eventSystem.publishEvent(Event.DetectorsComplete, detectorToolResult);
    }

    public void publishExtractionCount(Integer extractionCount) {
        eventSystem.publishEvent(Event.ExtractionCount, extractionCount);
    }

    public void publishUnrecognizedPaths(UnrecognizedPaths unrecognizedPaths) {
        eventSystem.publishEvent(Event.UnrecognizedPaths, unrecognizedPaths);
    }
}
