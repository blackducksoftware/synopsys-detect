/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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

import java.util.List;

import com.blackducksoftware.integration.hub.detect.detector.Detector;

public class DetectorSearchRule {
    private final Detector detector;
    private final int maxDepth;
    private final boolean nestable;
    private final List<Detector> yieldsTo;

    public DetectorSearchRule(final Detector detector, final int maxDepth, final boolean nestable, final List<Detector> yieldsTo) {
        this.detector = detector;
        this.maxDepth = maxDepth;
        this.nestable = nestable;
        this.yieldsTo = yieldsTo;
    }

    public Detector getDetector() {
        return detector;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public boolean isNestable() {
        return nestable;
    }

    public List<Detector> getYieldsTo() {
        return yieldsTo;
    }
}
