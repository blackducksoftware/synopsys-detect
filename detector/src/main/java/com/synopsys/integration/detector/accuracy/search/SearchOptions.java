package com.synopsys.integration.detector.accuracy.search;

import java.util.Set;
import java.util.function.Predicate;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;

public class SearchEnvironment {
    private final int depth;
    private final Predicate<DetectorType> detectorFilter;
    private final boolean forceNestedSearch;
    private final boolean followSymLinks;

    private final Set<DetectorRule> appliedToParent;
    private final Set<DetectorRule> appliedSoFar;

    public SearchEnvironment(
        int depth,
        Predicate<DetectorType> detectorFilter,
        boolean forceNestedSearch,
        boolean followSymLinks,
        Set<DetectorRule> appliedToParent,
        Set<DetectorRule> appliedSoFar
    ) {
        this.depth = depth;
        this.detectorFilter = detectorFilter;
        this.forceNestedSearch = forceNestedSearch;
        this.followSymLinks = followSymLinks;
        this.appliedToParent = appliedToParent;
        this.appliedSoFar = appliedSoFar;
    }

    public int getDepth() {
        return depth;
    }

    public Predicate<DetectorType> getDetectorFilter() {
        return detectorFilter;
    }

    public boolean isForceNestedSearch() {
        return forceNestedSearch;
    }

    public boolean isFollowSymLinks() {
        return followSymLinks;
    }

    public Set<DetectorRule> getAppliedToParent() {
        return appliedToParent;
    }

    public Set<DetectorRule> getAppliedSoFar() {
        return appliedSoFar;
    }
}
