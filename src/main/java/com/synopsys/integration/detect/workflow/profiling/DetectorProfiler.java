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
package com.synopsys.integration.detect.workflow.profiling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectorProfiler {
    private final Timekeeper<DetectorEvaluation> applicableTimekeeper = new Timekeeper<>();
    private final Timekeeper<DetectorEvaluation> extractableTimekeeper = new Timekeeper<>();
    private final Timekeeper<DetectorEvaluation> discoveryTimekeeper = new Timekeeper<>();
    private final Timekeeper<DetectorEvaluation> extractionTimekeeper = new Timekeeper<>();

    private final EventSystem eventSystem;

    public DetectorProfiler(EventSystem eventSystem) {
        this.eventSystem = eventSystem;

        eventSystem.registerListener(Event.ApplicableStarted, this::applicableStarted);
        eventSystem.registerListener(Event.ApplicableEnded, this::applicableEnded);
        eventSystem.registerListener(Event.ExtractableStarted, this::extractableStarted);
        eventSystem.registerListener(Event.ExtractableEnded, this::extractableEnded);
        eventSystem.registerListener(Event.DiscoveryStarted, this::discoveryStarted);
        eventSystem.registerListener(Event.DiscoveryEnded, this::discoveryEnded);
        eventSystem.registerListener(Event.ExtractionStarted, this::extractionStarted);
        eventSystem.registerListener(Event.ExtractionEnded, this::extractionEnded);
        eventSystem.registerListener(Event.DetectorsComplete, event -> detectorsComplete());
    }

    private void applicableStarted(DetectorEvaluation evaluation) {
        applicableTimekeeper.started(evaluation);
    }

    private void applicableEnded(DetectorEvaluation evaluation) {
        applicableTimekeeper.ended(evaluation);
    }

    private void extractableStarted(DetectorEvaluation evaluation) {
        extractableTimekeeper.started(evaluation);
    }

    private void extractableEnded(DetectorEvaluation evaluation) {
        extractableTimekeeper.ended(evaluation);
    }

    private void discoveryStarted(DetectorEvaluation evaluation) {
        discoveryTimekeeper.started(evaluation);
    }

    private void discoveryEnded(DetectorEvaluation evaluation) {
        discoveryTimekeeper.ended(evaluation);
    }

    private void extractionStarted(DetectorEvaluation evaluation) {
        extractionTimekeeper.started(evaluation);
    }

    private void extractionEnded(DetectorEvaluation evaluation) {
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

    public List<Timing<DetectorEvaluation>> getDiscoveryTimings() {
        return extractionTimekeeper.getTimings();
    }

    public void detectorsComplete() {
        DetectorTimings timings = new DetectorTimings(getAggregateDetectorGroupTimes(), getApplicableTimings(), getExtractableTimings(), getDiscoveryTimings(), getExtractionTimings());
        eventSystem.publishEvent(Event.DetectorsProfiled, timings);
    }

    private void addAggregateByDetectorGroupType(Map<DetectorType, Long> aggregate, List<Timing<DetectorEvaluation>> timings) {
        for (Timing<DetectorEvaluation> timing : timings) {
            DetectorType type = timing.getKey().getDetectorType();
            if (!aggregate.containsKey(type)) {
                aggregate.put(type, 0L);
            }
            long time = timing.getMs();
            Long currentTime = aggregate.get(type);
            Long sum = time + currentTime;
            aggregate.put(type, sum);
        }
    }

    public Map<DetectorType, Long> getAggregateDetectorGroupTimes() {
        Map<DetectorType, Long> aggregate = new HashMap<>();
        addAggregateByDetectorGroupType(aggregate, getExtractableTimings());
        addAggregateByDetectorGroupType(aggregate, getDiscoveryTimings());
        addAggregateByDetectorGroupType(aggregate, getExtractionTimings());
        return aggregate;
    }
}
