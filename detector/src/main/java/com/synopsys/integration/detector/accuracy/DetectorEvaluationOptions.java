package com.synopsys.integration.detector.accuracy;

import java.util.function.Predicate;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorEvaluationOptions {
    private final boolean forceNested;
    private final boolean followSymLinks;
    private final Predicate<DetectorType> detectorFilter;

    public DetectorEvaluationOptions(boolean forceNested, boolean followSymLinks, Predicate<DetectorType> detectorFilter) {
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

    public Predicate<DetectorType> getDetectorFilter() {
        return detectorFilter;
    }
}
