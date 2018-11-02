package com.blackducksoftware.integration.hub.detect.workflow.profiling;

import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;

public class BomToolAggregateTimings {
    public Map<DetectorType, Long> bomToolTimings = new HashMap<>();
}
