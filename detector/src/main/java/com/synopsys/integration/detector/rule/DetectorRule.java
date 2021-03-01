/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.rule;

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
    private final DetectorType detectorType;
    private final String name;
    private final boolean nestInvisible;

    public DetectorRule(DetectableCreatable detectableCreatable, Class<T> detectableClass, int maxDepth, boolean nestable, boolean selfNestable, DetectorType detectorType, String name,
        boolean nestInvisible) {
        this.detectableCreatable = detectableCreatable;
        this.detectableClass = detectableClass;
        this.maxDepth = maxDepth;
        this.nestable = nestable;
        this.selfNestable = selfNestable;
        this.detectorType = detectorType;
        this.name = name;
        this.nestInvisible = nestInvisible;
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

    public Class<T> getDetectableClass() {
        return detectableClass;
    }
}
