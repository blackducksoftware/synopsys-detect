package com.synopsys.integration.detector.accuracy;

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
