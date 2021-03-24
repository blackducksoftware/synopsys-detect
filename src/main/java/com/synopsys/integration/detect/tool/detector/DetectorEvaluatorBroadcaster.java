/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.evaluation.DetectorEvaluatorListener;

public class DetectorEvaluatorBroadcaster implements DetectorEvaluatorListener {
    private final EventSystem eventSystem;

    public DetectorEvaluatorBroadcaster(final EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    @Override
    public void applicableStarted(final DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ApplicableStarted, detectorEvaluation);
    }

    @Override
    public void applicableEnded(final DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ApplicableEnded, detectorEvaluation);
    }

    @Override
    public void extractableStarted(final DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ExtractableStarted, detectorEvaluation);
    }

    @Override
    public void extractableEnded(final DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ExtractableEnded, detectorEvaluation);
    }

    @Override
    public void discoveryStarted(final DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.DiscoveryStarted, detectorEvaluation);
    }

    @Override
    public void discoveryEnded(final DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.DiscoveryEnded, detectorEvaluation);
    }

    @Override
    public void extractionStarted(final DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ExtractionStarted, detectorEvaluation);
    }

    @Override
    public void extractionEnded(final DetectorEvaluation detectorEvaluation) {
        eventSystem.publishEvent(Event.ExtractionEnded, detectorEvaluation);
    }
}
