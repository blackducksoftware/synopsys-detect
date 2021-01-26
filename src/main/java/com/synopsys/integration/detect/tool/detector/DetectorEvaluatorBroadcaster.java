/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
