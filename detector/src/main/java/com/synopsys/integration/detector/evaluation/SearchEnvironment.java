/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.evaluation;

import java.util.Set;
import java.util.function.Predicate;

import com.synopsys.integration.detector.rule.DetectorRule;

public class SearchEnvironment {
    private final int depth;
    private final Predicate<DetectorRule> detectorFilter;
    private final boolean forceNestedSearch;
    private final Set<DetectorRule> appliedToParent;
    private final Set<DetectorRule> appliedSoFar;

    public SearchEnvironment(final int depth, final Predicate<DetectorRule> detectorFilter, final boolean forceNestedSearch, final Set<DetectorRule> appliedToParent,
        final Set<DetectorRule> appliedSoFar) {
        this.depth = depth;
        this.detectorFilter = detectorFilter;
        this.forceNestedSearch = forceNestedSearch;
        this.appliedToParent = appliedToParent;
        this.appliedSoFar = appliedSoFar;
    }

    public int getDepth() {
        return depth;
    }

    public Predicate<DetectorRule> getDetectorFilter() {
        return detectorFilter;
    }

    public boolean isForceNestedSearch() {
        return forceNestedSearch;
    }

    public Set<DetectorRule> getAppliedToParent() {
        return appliedToParent;
    }

    public Set<DetectorRule> getAppliedSoFar() {
        return appliedSoFar;
    }
}
