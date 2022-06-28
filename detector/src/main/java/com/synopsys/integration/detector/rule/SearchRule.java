package com.synopsys.integration.detector.rule;

import java.util.Set;

import com.synopsys.integration.detector.base.DetectorType;

public class SearchRule {
    private final int maxDepth;
    private final boolean nestable;
    private final Set<DetectorType> notNestableBeneath;
    private final Set<DetectableDefinition> notNestableBeneathDetectables;
    private final Set<DetectorType> yieldsTo;

    public SearchRule(
        int maxDepth,
        boolean nestable,
        Set<DetectorType> notNestableBeneath,
        Set<DetectableDefinition> notNestableBeneathDetectables,
        Set<DetectorType> yieldsTo
    ) {
        this.maxDepth = maxDepth;
        this.nestable = nestable;
        this.notNestableBeneath = notNestableBeneath;
        this.notNestableBeneathDetectables = notNestableBeneathDetectables;
        this.yieldsTo = yieldsTo;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public boolean isNestable() {
        return nestable;
    }

    public Set<DetectorType> getNotNestableBeneath() {
        return notNestableBeneath;
    }

    public Set<DetectorType> getYieldsTo() {
        return yieldsTo;
    }

    public Set<DetectableDefinition> getNotNestableBeneathDetectables() {
        return notNestableBeneathDetectables;
    }
}
