package com.blackduck.integration.detect.tool.detector.file;

import java.util.function.Predicate;

import com.blackduck.integration.detect.util.filter.DetectOverrideableFilter;
import com.blackduck.integration.detector.rule.DetectorRule;

public class DetectDetectorFilter extends DetectOverrideableFilter implements Predicate<DetectorRule> {
    public DetectDetectorFilter(String excluded, String included) {
        super(excluded, included);
    }

    @Override
    public boolean test(DetectorRule detectorRule) {
        return shouldInclude(detectorRule.getDetectorType().toString());
    }
}
