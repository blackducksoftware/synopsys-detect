package com.synopsys.integration.detector.rule;

import java.util.Set;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detector.base.DetectableCreatable;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectorRule<T extends Detectable> {
    private final DetectableCreatable detectableCreatable;
    private final Class<T> detectableClass;

    private final int maxDepth;
    private final boolean nestable;
    private final boolean selfNestable;
    private final boolean selfTypeNestable;
    private final DetectorType detectorType;
    private final String name;
    private final boolean nestInvisible;
    private final Set<DetectorType> notNestableBeneath;

    public DetectorRule(
        DetectableCreatable detectableCreatable,
        Class<T> detectableClass,
        int maxDepth,
        boolean nestable,
        boolean selfNestable,
        boolean selfTypeNestable,
        DetectorType detectorType,
        String name,
        boolean nestInvisible,
        Set<DetectorType> notNestableBeneath
    ) {
        this.detectableCreatable = detectableCreatable;
        this.detectableClass = detectableClass;
        this.maxDepth = maxDepth;
        this.nestable = nestable;
        this.selfNestable = selfNestable;
        this.selfTypeNestable = selfTypeNestable;
        this.detectorType = detectorType;
        this.name = name;
        this.nestInvisible = nestInvisible;
        this.notNestableBeneath = notNestableBeneath;
    }

    public DetectableCreatable getDetectableCreatable() {
        return detectableCreatable;
    }

    public Detectable createDetectable(DetectableEnvironment detectableEnvironment) {
        return detectableCreatable.createDetectable(detectableEnvironment);
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

    public String getDescriptiveName() {
        return String.format("%s - %s", getDetectorType().toString(), getName());
    }

    public String getName() {
        return name;
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

    public Class<T> getDetectableClass() {
        return detectableClass;
    }

    public Set<DetectorType> getNotNestableBeneath() {
        return notNestableBeneath;
    }
}
