package com.synopsys.integration.detector.rule;

import java.util.List;
import java.util.Set;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorRule {
    private final int maxDepth;
    private final boolean nestable;
    private final boolean selfNestable;
    private final boolean selfTypeNestable;
    private final DetectorType detectorType;
    private final boolean nestInvisible;
    private final Set<DetectorType> notNestableBeneath;
    private final List<EntryPoint> entryPoints;
    private final Set<DetectorType> yieldsTo;

    public DetectorRule(
        int maxDepth,
        boolean nestable,
        boolean selfNestable,
        boolean selfTypeNestable,
        DetectorType detectorType,
        boolean nestInvisible,
        Set<DetectorType> notNestableBeneath,
        List<EntryPoint> entryPoints,
        Set<DetectorType> yieldsTo
    ) {
        this.maxDepth = maxDepth;
        this.nestable = nestable;
        this.selfNestable = selfNestable;
        this.selfTypeNestable = selfTypeNestable;
        this.detectorType = detectorType;
        this.nestInvisible = nestInvisible;
        this.notNestableBeneath = notNestableBeneath;
        this.entryPoints = entryPoints;
        this.yieldsTo = yieldsTo;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public boolean isNestable() {
        return nestable;
    }

    public DetectorType getDetectorType() {
        return detectorType;
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

    public List<EntryPoint> getEntryPoints() {
        return entryPoints;
    }

    public Set<DetectorType> getYieldsTo() {
        return yieldsTo;
    }
}
