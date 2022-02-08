package com.synopsys.integration.detect.tool.detector;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.evaluation.DetectorEvaluatorListener;

public class DetectorEvaluatorBroadcaster implements DetectorEvaluatorListener {
    private final EventSystem eventSystem;

    public DetectorEvaluatorBroadcaster(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    @Override
    public void applicableStarted(DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ApplicableStarted, detectorEvaluation);
    }

    @Override
    public void applicableEnded(DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ApplicableEnded, detectorEvaluation);
    }

    @Override
    public void extractableStarted(DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ExtractableStarted, detectorEvaluation);
    }

    @Override
    public void extractableEnded(DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ExtractableEnded, detectorEvaluation);
    }

    @Override
    public void extractionStarted(DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ExtractionStarted, detectorEvaluation);
    }

    @Override
    public void extractionEnded(DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ExtractionEnded, detectorEvaluation);
    }
}
