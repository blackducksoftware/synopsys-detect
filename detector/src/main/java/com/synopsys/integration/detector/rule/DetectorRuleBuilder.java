/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

    public DetectorRuleBuilder(final String name, final DetectorType detectorType, final Class<T> detectableClass, final DetectableCreatable<T> detectableCreatable) {
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

    public DetectorRuleBuilder maxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public DetectorRuleBuilder isNestable(final boolean nestable) {
        this.nestable = nestable;
        return this;
    }

    public DetectorRuleBuilder isSelfNestable(final boolean selfNestable) {
        this.selfNestable = selfNestable;
        return this;
    }

    public DetectorRuleBuilder isNestInvisible(final boolean nestable) {
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
        final DetectorRule rule = new DetectorRule(detectableCreatable, detectableClass, maxDepth, nestable, selfNestable, detectorType, name, nestInvisible);
        if (detectorRuleSetBuilder != null) {
            detectorRuleSetBuilder.add(rule);
        }
        return rule;
    }

    public void setDetectorRuleSetBuilder(final DetectorRuleSetBuilder detectorRuleSetBuilder) {
        this.detectorRuleSetBuilder = detectorRuleSetBuilder;
    }

    public Class<T> getDetectableClass() {
        return detectableClass;
    }
}
