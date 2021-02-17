/*
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

import java.util.List;
import java.util.Map;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectorTimings {
    private final Map<DetectorType, Long> aggregateTimings;
    private final List<Timing<DetectorEvaluation>> applicableTimings;
    private final List<Timing<DetectorEvaluation>> extractableTimings;
    private final List<Timing<DetectorEvaluation>> discoveryTimings;
    private final List<Timing<DetectorEvaluation>> extractionTimings;

    public DetectorTimings(final Map<DetectorType, Long> aggregateTimings, final List<Timing<DetectorEvaluation>> applicableTimings,
        final List<Timing<DetectorEvaluation>> extractableTimings, final List<Timing<DetectorEvaluation>> discoveryTimings,
        final List<Timing<DetectorEvaluation>> extractionTimings) {
        this.aggregateTimings = aggregateTimings;
        this.applicableTimings = applicableTimings;
        this.extractableTimings = extractableTimings;
        this.discoveryTimings = discoveryTimings;
        this.extractionTimings = extractionTimings;
    }

    public Map<DetectorType, Long> getAggregateTimings() {
        return aggregateTimings;
    }

    public List<Timing<DetectorEvaluation>> getApplicableTimings() {
        return applicableTimings;
    }

    public List<Timing<DetectorEvaluation>> getExtractableTimings() {
        return extractableTimings;
    }

    public List<Timing<DetectorEvaluation>> getExtractionTimings() {
        return extractionTimings;
    }

    public List<Timing<DetectorEvaluation>> getDiscoveryTimings() {
        return discoveryTimings;
    }
}
