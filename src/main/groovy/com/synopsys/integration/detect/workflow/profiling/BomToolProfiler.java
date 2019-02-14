/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detect.workflow.profiling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class BomToolProfiler {
    public BomToolTimekeeper applicableTimekeeper = new BomToolTimekeeper();
    public BomToolTimekeeper extractableTimekeeper = new BomToolTimekeeper();
    public BomToolTimekeeper extractionTimekeeper = new BomToolTimekeeper();
    private EventSystem eventSystem;

    public BomToolProfiler(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
        eventSystem.registerListener(Event.ApplicableStarted, event -> applicableStarted(event));
        eventSystem.registerListener(Event.ApplicableEnded, event -> applicableEnded(event));
        eventSystem.registerListener(Event.ExtractableStarted, event -> extractableStarted(event));
        eventSystem.registerListener(Event.ExtractableEnded, event -> extractableEnded(event));
        eventSystem.registerListener(Event.ExtractionStarted, event -> extractionStarted(event.getDetector()));
        eventSystem.registerListener(Event.ExtractionEnded, event -> extractionEnded(event.getDetector()));
        eventSystem.registerListener(Event.DetectorsComplete, event -> bomToolsComplete());
    }

    private void applicableStarted(final Detector detector) {
        applicableTimekeeper.started(detector);
    }

    private void applicableEnded(final Detector detector) {
        applicableTimekeeper.ended(detector);
    }

    private void extractableStarted(final Detector detector) {
        extractableTimekeeper.started(detector);
    }

    private void extractableEnded(final Detector detector) {
        extractableTimekeeper.ended(detector);
    }

    private void extractionStarted(final Detector detector) {
        extractionTimekeeper.started(detector);
    }

    private void extractionEnded(final Detector detector) {
        extractionTimekeeper.ended(detector);
    }

    public List<DetectorTime> getApplicableTimings() {
        return applicableTimekeeper.getTimings();
    }

    public List<DetectorTime> getExtractableTimings() {
        return extractableTimekeeper.getTimings();
    }

    public List<DetectorTime> getExtractionTimings() {
        return extractionTimekeeper.getTimings();
    }

    public void bomToolsComplete() {
        DetectorTimings timings = new DetectorTimings(getAggregateBomToolGroupTimes(), getApplicableTimings(), getExtractableTimings(), getExtractionTimings());
        eventSystem.publishEvent(Event.DetectorsProfiled, timings);
    }

    public Map<DetectorType, Long> getAggregateBomToolGroupTimes() {
        final Map<DetectorType, Long> aggregate = new HashMap<>();
        addAggregateByBomToolGroupType(aggregate, getExtractableTimings());
        addAggregateByBomToolGroupType(aggregate, getExtractionTimings());
        return aggregate;
    }

    private void addAggregateByBomToolGroupType(final Map<DetectorType, Long> aggregate, final List<DetectorTime> detectorTimes) {
        for (final DetectorTime detectorTime : detectorTimes) {
            final DetectorType type = detectorTime.getDetector().getDetectorType();
            if (!aggregate.containsKey(type)) {
                aggregate.put(type, 0L);
            }
            final long time = detectorTime.getMs();
            final Long currentTime = aggregate.get(type);
            final Long sum = time + currentTime;
            aggregate.put(type, sum);
        }
    }

}
