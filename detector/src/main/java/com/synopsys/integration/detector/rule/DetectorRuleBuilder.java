package com.synopsys.integration.detector.rule;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detector.base.DetectableCreatable;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectorRuleBuilder<T extends Detectable> {
    private final DetectableCreatable detectableCreatable;
    private final Class<T> detectableClass;
    private int maxDepth;
    private boolean nestable;
    private boolean selfNestable = false;
    private boolean nestInvisible = false;

    private final String name;
    private final DetectorType detectorType;

    private DetectorRuleSetBuilder detectorRuleSetBuilder;

    public DetectorRuleBuilder(String name, DetectorType detectorType, Class<T> detectableClass, DetectableCreatable<T> detectableCreatable) {
        this.name = name;
        this.detectorType = detectorType;
        this.detectableCreatable = detectableCreatable;
        this.detectableClass = detectableClass;
    }

    public DetectorRuleBuilder defaults() {
        return noMaxDepth().nestable().notSelfNestable().visibleToNesting();
    }

    public DetectorRuleBuilder defaultLock() {
        return noMaxDepth().nestable().selfNestable().visibleToNesting();
    }

    public DetectorRuleBuilder noMaxDepth() {
        return maxDepth(Integer.MAX_VALUE);
    }

    public DetectorRuleBuilder maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public DetectorRuleBuilder isNestable(boolean nestable) {
        this.nestable = nestable;
        return this;
    }

    public DetectorRuleBuilder isSelfNestable(boolean selfNestable) {
        this.selfNestable = selfNestable;
        return this;
    }

    public DetectorRuleBuilder isNestInvisible(boolean nestable) {
        this.nestInvisible = nestable;
        return this;
    }

    public DetectorRuleBuilder invisibleToNesting() {
        return isNestInvisible(true);
    }

    public DetectorRuleBuilder visibleToNesting() {
        return isNestInvisible(false);
    }

    public DetectorRuleBuilder nestable() {
        return isNestable(true);
    }

    public DetectorRuleBuilder notNestable() {
        return isNestable(false);
    }

    public DetectorRuleBuilder selfNestable() {
        return isSelfNestable(true);
    }

    public DetectorRuleBuilder notSelfNestable() {
        return isSelfNestable(false);
    }

    public DetectorRule build() {
        DetectorRule rule = new DetectorRule(detectableCreatable, detectableClass, maxDepth, nestable, selfNestable, detectorType, name, nestInvisible);
        if (detectorRuleSetBuilder != null) {
            detectorRuleSetBuilder.add(rule);
        }
        return rule;
    }

    public void setDetectorRuleSetBuilder(DetectorRuleSetBuilder detectorRuleSetBuilder) {
        this.detectorRuleSetBuilder = detectorRuleSetBuilder;
    }

    public Class<T> getDetectableClass() {
        return detectableClass;
    }
}
