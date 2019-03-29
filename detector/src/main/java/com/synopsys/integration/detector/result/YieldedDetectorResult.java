/**
 * detector
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detector.result;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detector.rule.DetectorRule;

public class YieldedDetectorResult extends FailedDetectorResult {
    private final Set<DetectorRule> yieldedTo;

    public YieldedDetectorResult(final DetectorRule yielded) {
        yieldedTo = new HashSet<>();
        yieldedTo.add(yielded);
    }

    public YieldedDetectorResult(final Set<DetectorRule> yieldedTo) {
        this.yieldedTo = yieldedTo;
    }

    @Override
    public String toDescription() {
        //TODO: Put in using some property on the rule.
        //final String yielded = yieldedTo.stream().map(it -> it.getDescriptiveName()).collect(Collectors.joining(", "));
        return "Yielded to detectors: ";// + yielded;
    }
}
