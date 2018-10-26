package com.blackducksoftware.integration.hub.detect.workflow.profiling;

import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;

public class BomToolAggregateTimings {
    public Map<BomToolGroupType, Long> bomToolTimings = new HashMap<>();
}
