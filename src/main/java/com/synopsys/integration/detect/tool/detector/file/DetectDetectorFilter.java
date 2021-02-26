/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.file;

import java.util.function.Predicate;

import com.synopsys.integration.detect.util.filter.DetectOverrideableFilter;
import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectDetectorFilter extends DetectOverrideableFilter implements Predicate<DetectorRule> {
    public DetectDetectorFilter(String excluded, String included) {
        super(excluded, included);
    }

    @Override
    public boolean test(final DetectorRule detectorRule) {
        return shouldInclude(detectorRule.getDetectorType().toString());
    }
}
