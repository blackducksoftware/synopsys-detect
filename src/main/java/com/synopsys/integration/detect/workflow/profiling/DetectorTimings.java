/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
