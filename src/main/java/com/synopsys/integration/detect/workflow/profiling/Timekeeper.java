/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
