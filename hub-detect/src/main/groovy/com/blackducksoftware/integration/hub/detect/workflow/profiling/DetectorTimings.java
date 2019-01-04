/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow.profiling;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;

public class DetectorTimings {
    private final Map<DetectorType, Long> aggregateTimings;
    private final List<DetectorTime> applicableTimings;
    private final List<DetectorTime> extractableTimings;
    private final List<DetectorTime> extractionTimings;

    public DetectorTimings(final Map<DetectorType, Long> aggregateTimings, final List<DetectorTime> applicableTimings,
        final List<DetectorTime> extractableTimings, final List<DetectorTime> extractionTimings) {
        this.aggregateTimings = aggregateTimings;
        this.applicableTimings = applicableTimings;
        this.extractableTimings = extractableTimings;
        this.extractionTimings = extractionTimings;
    }

    public Map<DetectorType, Long> getAggregateTimings() {
        return aggregateTimings;
    }

    public List<DetectorTime> getApplicableTimings() {
        return applicableTimings;
    }

    public List<DetectorTime> getExtractableTimings() {
        return extractableTimings;
    }

    public List<DetectorTime> getExtractionTimings() {
        return extractionTimings;
    }

}
