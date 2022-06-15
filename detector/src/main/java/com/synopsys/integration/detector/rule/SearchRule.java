package com.synopsys.integration.detector.rule;

import java.util.Set;

import com.synopsys.integration.detector.base.DetectorType;

public class SearchRule {
    private final int maxDepth;
    private final boolean nestable;
    private final boolean selfNestable;
    private final boolean selfTypeNestable;
    private final boolean nestInvisible;
    private final Set<DetectorType> notNestableBeneath;
    private final Set<Class<?>> notNestableBeneathDetectables;
    private final Set<DetectorType> yieldsTo;

    public SearchRule(
        int maxDepth,
        boolean nestable,
        boolean selfNestable,
        boolean selfTypeNestable,
        boolean nestInvisible,
        Set<DetectorType> notNestableBeneath,
        Set<Class<?>> notNestableBeneathDetectables,
        Set<DetectorType> yieldsTo
    ) {
        this.maxDepth = maxDepth;
        this.nestable = nestable;
        this.selfNestable = selfNestable;
        this.selfTypeNestable = selfTypeNestable;
        this.nestInvisible = nestInvisible;
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

    public boolean isNestInvisible() {
        return nestInvisible;
    }

    public boolean isSelfNestable() {
        return selfNestable;
    }

    public boolean isSelfTypeNestable() {
        return selfTypeNestable;
    }

    public Set<DetectorType> getNotNestableBeneath() {
        return notNestableBeneath;
    }

    public Set<DetectorType> getYieldsTo() {
        return yieldsTo;
    }

    public Set<Class<?>> getNotNestableBeneathDetectables() {
        return notNestableBeneathDetectables;
    }
}
