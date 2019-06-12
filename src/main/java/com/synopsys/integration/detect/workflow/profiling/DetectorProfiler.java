/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class DetectorProfiler {
    public Timekeeper<DetectorEvaluation> applicableTimekeeper = new Timekeeper();
    public Timekeeper<DetectorEvaluation> extractableTimekeeper = new Timekeeper();
    public Timekeeper<DetectorEvaluation> extractionTimekeeper = new Timekeeper();
    private EventSystem eventSystem;

    public DetectorProfiler(EventSystem eventSystem) {
        this.eventSystem = eventSystem;

        eventSystem.registerListener(Event.ApplicableStarted, event -> applicableStarted(event));
        eventSystem.registerListener(Event.ApplicableEnded, event -> applicableEnded(event));
        eventSystem.registerListener(Event.ExtractableStarted, event -> extractableStarted(event));
        eventSystem.registerListener(Event.ExtractableEnded, event -> extractableEnded(event));
        eventSystem.registerListener(Event.ExtractionStarted, event -> extractionStarted(event));
        eventSystem.registerListener(Event.ExtractionEnded, event -> extractionEnded(event));
        eventSystem.registerListener(Event.DetectorsComplete, event -> bomToolsComplete());
    }


    private void applicableStarted(final DetectorEvaluation evaluation) {
        applicableTimekeeper.started(evaluation);
    }

    private void applicableEnded(final DetectorEvaluation evaluation) {
        applicableTimekeeper.ended(evaluation);
    }

    private void extractableStarted(final DetectorEvaluation evaluation) {
        extractableTimekeeper.started(evaluation);
    }

    private void extractableEnded(final DetectorEvaluation evaluation) {
        extractableTimekeeper.ended(evaluation);
    }

    private void extractionStarted(final DetectorEvaluation evaluation) {
        extractionTimekeeper.started(evaluation);
    }

    private void extractionEnded(final DetectorEvaluation evaluation) {
        extractionTimekeeper.ended(evaluation);
    }

    public List<Timing<DetectorEvaluation>> getApplicableTimings() {
        return applicableTimekeeper.getTimings();
    }

    public List<Timing<DetectorEvaluation>> getExtractableTimings() {
        return extractableTimekeeper.getTimings();
    }

    public List<Timing<DetectorEvaluation>> getExtractionTimings() {
        return extractionTimekeeper.getTimings();
    }

    public void bomToolsComplete() {
        DetectorTimings timings = new DetectorTimings(getAggregateBomToolGroupTimes(), getApplicableTimings(), getExtractableTimings(), getExtractionTimings());
        eventSystem.publishEvent(Event.DetectorsProfiled, timings);
    }

    private void addAggregateByBomToolGroupType(final Map<DetectorType, Long> aggregate, final List<Timing<DetectorEvaluation>> timings) {
        for (final Timing<DetectorEvaluation> timing : timings) {
            final DetectorType type = timing.getKey().getDetectorRule().getDetectorType();
            if (!aggregate.containsKey(type)) {
                aggregate.put(type, 0L);
            }
            final long time = timing.getMs();
            final Long currentTime = aggregate.get(type);
            final Long sum = time + currentTime;
            aggregate.put(type, sum);
        }
    }

    public Map<DetectorType, Long> getAggregateBomToolGroupTimes() {
        final Map<DetectorType, Long> aggregate = new HashMap<>();
        addAggregateByBomToolGroupType(aggregate, getExtractableTimings());
        addAggregateByBomToolGroupType(aggregate, getExtractionTimings());
        return aggregate;
    }
}
