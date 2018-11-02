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
package com.blackducksoftware.integration.hub.detect.workflow.search.result;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.detector.Detector;

public class YieldedDetectorResult extends FailedDetectorResult {
    private final Set<Detector> yieldedTo;

    public YieldedDetectorResult(final Detector yielded) {
        yieldedTo = new HashSet<>();
        yieldedTo.add(yielded);
    }

    public YieldedDetectorResult(final Set<Detector> yieldedTo) {
        this.yieldedTo = yieldedTo;
    }

    @Override
    public String toDescription() {
        final String yielded = yieldedTo.stream().map(it -> it.getDescriptiveName()).collect(Collectors.joining(", "));
        return "Yielded to bom tools: " + yielded;
    }
}
