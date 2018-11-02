/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.workflow.search.rules;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.detector.Detector;

public class DetectorSearchRuleBuilder {
    private final Detector detector;
    private int maxDepth;
    private boolean nestable;
    private final List<Detector> yieldsTo;

    public DetectorSearchRuleBuilder(final Detector detector) {
        this.detector = detector;
        yieldsTo = new ArrayList<>();
    }

    public DetectorSearchRuleBuilder defaultNotNested() {
        maxDepth(Integer.MAX_VALUE);
        return nestable(false);
    }

    public DetectorSearchRuleBuilder defaultNested() {
        maxDepth(Integer.MAX_VALUE);
        return nestable(true);
    }

    public DetectorSearchRuleBuilder maxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public DetectorSearchRuleBuilder nestable(final boolean nestable) {
        this.nestable = nestable;
        return this;
    }

    public DetectorSearchRuleBuilder yield(final Detector type) {
        this.yieldsTo.add(type);
        return this;
    }

    public DetectorSearchRule build() {
        return new DetectorSearchRule(detector, maxDepth, nestable, yieldsTo);
    }
}
