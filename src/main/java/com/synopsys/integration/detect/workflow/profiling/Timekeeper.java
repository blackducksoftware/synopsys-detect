/*
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;

public class Timekeeper<T> {

    private final Map<T, StopWatch> stopWatches = new HashMap<>();

    private StopWatch getStopWatch(final T key) {
        if (stopWatches.containsKey(key)) {
            return stopWatches.get(key);
        } else {
            final StopWatch sw = new StopWatch();
            stopWatches.put(key, sw);
            return sw;
        }
    }

    public void started(final T key) {
        getStopWatch(key).start();
    }

    public void ended(final T key) {
        getStopWatch(key).stop();
    }

    public List<Timing<T>> getTimings() {
        final List<Timing<T>> bomToolTimings = new ArrayList<>();
        for (final T key : stopWatches.keySet()) {
            final StopWatch sw = stopWatches.get(key);
            final long ms = sw.getTime();
            final Timing timing = new Timing(key, ms);
            bomToolTimings.add(timing);
        }
        return bomToolTimings;
    }
}
