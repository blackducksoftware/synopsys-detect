/*
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
