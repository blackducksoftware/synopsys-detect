package com.synopsys.integration.detector.accuracy.search;

import java.util.Set;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectableDefinition;

public class SearchEnvironment {
    private final int depth;
    private final Set<DetectorType> appliedToParent;
    private final Set<DetectableDefinition> extractedInParent;
    private final Set<DetectorType> appliedSoFar;

    public SearchEnvironment(
        int depth,
        Set<DetectorType> appliedSoFar,
        Set<DetectorType> appliedToParent,
        Set<DetectableDefinition> extractedInParent
    ) {
        this.depth = depth;
        this.appliedSoFar = appliedSoFar;
        this.appliedToParent = appliedToParent;
        this.extractedInParent = extractedInParent;
    }

    public int getDepth() {
        return depth;
    }

    public Set<DetectorType> getAppliedToParent() {
        return appliedToParent;
    }

    public Set<DetectableDefinition> getExtractedInParent() {
        return extractedInParent;
    }

    public Set<DetectorType> getAppliedSoFar() {
        return appliedSoFar;
    }
}
