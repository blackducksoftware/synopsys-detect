package com.synopsys.integration.detector.accuracy.search;

import java.util.Set;

import com.synopsys.integration.detector.rule.DetectorRule;

public class SearchEnvironment {
    private final int depth;
    private final Set<DetectorRule> appliedToParent;
    private final Set<DetectorRule> appliedSoFar;

    public SearchEnvironment(
        int depth,
        Set<DetectorRule> appliedToParent,
        Set<DetectorRule> appliedSoFar
    ) {
        this.depth = depth;
        this.appliedToParent = appliedToParent;
        this.appliedSoFar = appliedSoFar;
    }

    public int getDepth() {
        return depth;
    }

    public Set<DetectorRule> getAppliedToParent() {
        return appliedToParent;
    }

    public Set<DetectorRule> getAppliedSoFar() {
        return appliedSoFar;
    }
}
