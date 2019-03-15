package com.synopsys.integration.detect.tool.detector.impl;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.finder.DetectorFilter;
import com.synopsys.integration.util.ExcludedIncludedFilter;

public class DetectDetectorFilter implements DetectorFilter {
    private final ExcludedIncludedFilter filter;
    public DetectDetectorFilter(String excluded, String included){
        filter = new ExcludedIncludedFilter(excluded, included);
    }

    @Override
    public boolean shouldInclude(final DetectorType detectorType) {
        return filter.shouldInclude(detectorType.toString());
    }
}
