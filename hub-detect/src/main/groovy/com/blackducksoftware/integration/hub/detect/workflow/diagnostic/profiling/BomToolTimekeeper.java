package com.blackducksoftware.integration.hub.detect.workflow.diagnostic.profiling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;

public class BomToolTimekeeper {

    private final Map<BomTool, StopWatch> bomToolMap = new HashMap<>();

    private StopWatch getStopWatch(final BomTool bomTool) {
        if (bomToolMap.containsKey(bomTool)) {
            return bomToolMap.get(bomTool);
        } else {
            final StopWatch sw = new StopWatch();
            bomToolMap.put(bomTool, sw);
            return sw;
        }
    }

    public void started(final BomTool bomTool) {
        getStopWatch(bomTool).start();
    }

    public void ended(final BomTool bomTool) {
        getStopWatch(bomTool).stop();
    }

    public List<BomToolTime> getTimings() {
        final List<BomToolTime> bomToolTimings = new ArrayList<>();
        for (final BomTool bomTool : bomToolMap.keySet()) {
            final StopWatch sw = bomToolMap.get(bomTool);
            final long ms = sw.getTime();
            final BomToolTime bomToolTime = new BomToolTime(bomTool, ms);
            bomToolTimings.add(bomToolTime);
        }
        return bomToolTimings;
    }
}
