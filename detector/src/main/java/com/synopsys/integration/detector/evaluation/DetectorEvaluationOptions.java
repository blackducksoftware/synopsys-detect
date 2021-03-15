/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.evaluation;

import java.util.function.Predicate;

import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectorEvaluationOptions {
    private final boolean forceNested;
    private final Predicate<DetectorRule> detectorFilter;

    public DetectorEvaluationOptions(final boolean forceNested, final Predicate<DetectorRule> detectorFilter) {
        this.forceNested = forceNested;
        this.detectorFilter = detectorFilter;
    }

    public boolean isForceNested() {
        return forceNested;
    }

    public Predicate<DetectorRule> getDetectorFilter() {
        return detectorFilter;
    }
}
