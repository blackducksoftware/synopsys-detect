package com.synopsys.integration.detect.tool.detector.impl;

import java.util.function.Predicate;

import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;
import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectDetectorFilter extends DetectOverrideableFilter implements Predicate<DetectorRule> {
    public DetectDetectorFilter(String excluded, String included){
        super(excluded, included);
    }

    @Override
    public boolean test(final DetectorRule detectorRule) {
        return shouldInclude(detectorRule.getDetectorType().toString());
    }
}
