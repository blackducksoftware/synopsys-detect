/**
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
