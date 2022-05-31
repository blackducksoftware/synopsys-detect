package com.synopsys.integration.detect.workflow.profiling;

import java.util.List;
import java.util.Map;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorTimings {
    private final Map<DetectorType, Long> aggregateTimings;
    private final List<Timing<DetectorType>> applicableTimings;
    private final List<Timing<DetectorType>> extractableTimings;
    private final List<Timing<DetectorType>> extractionTimings;

    public DetectorTimings(
        Map<DetectorType, Long> aggregateTimings,
        List<Timing<DetectorType>> applicableTimings,
        List<Timing<DetectorType>> extractableTimings,
        List<Timing<DetectorType>> extractionTimings
    ) {
        this.aggregateTimings = aggregateTimings;
        this.applicableTimings = applicableTimings;
        this.extractableTimings = extractableTimings;
        this.extractionTimings = extractionTimings;
    }

    public Map<DetectorType, Long> getAggregateTimings() {
        return aggregateTimings;
    }

    public List<Timing<DetectorType>> getApplicableTimings() {
        return applicableTimings;
    }

    public List<Timing<DetectorType>> getExtractableTimings() {
        return extractableTimings;
    }

    public List<Timing<DetectorType>> getExtractionTimings() {
        return extractionTimings;
    }
}
