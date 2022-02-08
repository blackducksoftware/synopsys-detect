package com.synopsys.integration.detect.workflow.profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;

public class Timekeeper<T> {

    private final Map<T, StopWatch> stopWatches = new HashMap<>();

    private StopWatch getStopWatch(T key) {
        if (stopWatches.containsKey(key)) {
            return stopWatches.get(key);
        } else {
            StopWatch sw = new StopWatch();
            stopWatches.put(key, sw);
            return sw;
        }
    }

    public void started(T key) {
        getStopWatch(key).start();
    }

    public void ended(T key) {
        getStopWatch(key).stop();
    }

    public List<Timing<T>> getTimings() {
        List<Timing<T>> bomToolTimings = new ArrayList<>();
        for (T key : stopWatches.keySet()) {
            StopWatch sw = stopWatches.get(key);
            long ms = sw.getTime();
            Timing timing = new Timing(key, ms);
            bomToolTimings.add(timing);
        }
        return bomToolTimings;
    }
}
