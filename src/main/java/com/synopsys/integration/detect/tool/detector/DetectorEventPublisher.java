package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.Set;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.status.UnrecognizedPaths;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectorEventPublisher {
    private final EventSystem eventSystem;

    public DetectorEventPublisher(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    public void publishApplicableCompleted(Set<DetectorType> applicableDetectors) {
        eventSystem.publishEvent(Event.ApplicableCompleted, applicableDetectors);
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

    public void publishExtractionsCompleted(DetectorEvaluationTree detectorEvaluationTree) {
        eventSystem.publishEvent(Event.ExtractionsCompleted, detectorEvaluationTree);
    }

    public void publishPreparationsCompleted(DetectorEvaluationTree detectorEvaluationTree) {
        eventSystem.publishEvent(Event.PreparationsCompleted, detectorEvaluationTree);
    }

    public void publishSearchCompleted(DetectorEvaluationTree detectorEvaluationTree) {
        eventSystem.publishEvent(Event.SearchCompleted, detectorEvaluationTree);
    }

    public void publishUnrecognizedPaths(UnrecognizedPaths unrecognizedPaths) {
        eventSystem.publishEvent(Event.UnrecognizedPaths, unrecognizedPaths);
    }
}
