/*
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
    private final boolean followSymLinks;
    private final Predicate<DetectorRule> detectorFilter;

    public DetectorEvaluationOptions(boolean forceNested, boolean followSymLinks, Predicate<DetectorRule> detectorFilter) {
        this.forceNested = forceNested;
        this.followSymLinks = followSymLinks;
        this.detectorFilter = detectorFilter;
    }

    public boolean isForceNested() {
        return forceNested;
    }

    public boolean isFollowSymLinks() {
        return followSymLinks;
    }

    public Predicate<DetectorRule> getDetectorFilter() {
        return detectorFilter;
    }
}
