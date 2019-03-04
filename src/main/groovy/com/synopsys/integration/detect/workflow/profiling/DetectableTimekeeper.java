/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;

import com.synopsys.integration.detectable.Detectable;

public class DetectableTimekeeper {

    private final Map<Detectable, StopWatch> stopWatches = new HashMap<>();

    private StopWatch getStopWatch(final Detectable detector) {
        if (stopWatches.containsKey(detector)) {
            return stopWatches.get(detector);
        } else {
            final StopWatch sw = new StopWatch();
            stopWatches.put(detector, sw);
            return sw;
        }
    }

    public void started(final Detectable detector) {
        getStopWatch(detector).start();
    }

    public void ended(final Detectable detector) {
        getStopWatch(detector).stop();
    }

    public List<DetectableTime> getTimings() {
        final List<DetectableTime> bomToolTimings = new ArrayList<>();
        for (final Detectable detector : stopWatches.keySet()) {
            final StopWatch sw = stopWatches.get(detector);
            final long ms = sw.getTime();
            final DetectableTime detectorTime = new DetectableTime(detector, ms);
            bomToolTimings.add(detectorTime);
        }
        return bomToolTimings;
    }
}
