/**
 * detector
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
package com.synopsys.integration.detector.evaluation;

import java.util.Set;

import com.synopsys.integration.detector.finder.DetectorFilter;
import com.synopsys.integration.detector.rule.DetectorRule;

public class SearchEnvironment {
    private final int depth;
    private final DetectorFilter detectorFilter;
    private final boolean forceNestedSearch;
    private final Set<DetectorRule> appliedToParent;
    private final Set<DetectorRule> appliedSoFar;

    public SearchEnvironment(final int depth, final DetectorFilter detectorFilter, final boolean forceNestedSearch, final Set<DetectorRule> appliedToParent,
        final Set<DetectorRule> appliedSoFar) {
        this.depth = depth;
        this.detectorFilter = detectorFilter;
        this.forceNestedSearch = forceNestedSearch;
        this.appliedToParent = appliedToParent;
        this.appliedSoFar = appliedSoFar;
    }

    public int getDepth() {
        return depth;
    }

    public DetectorFilter getDetectorFilter() {
        return detectorFilter;
    }

    public boolean isForceNestedSearch() {
        return forceNestedSearch;
    }

    public Set<DetectorRule> getAppliedToParent() {
        return appliedToParent;
    }

    public Set<DetectorRule> getAppliedSoFar() {
        return appliedSoFar;
    }
}
