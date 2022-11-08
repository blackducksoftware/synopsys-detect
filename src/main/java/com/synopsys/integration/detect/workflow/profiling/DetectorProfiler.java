package com.synopsys.integration.detect.workflow.profiling;

import com.synopsys.integration.detect.workflow.event.EventSystem;

public class DetectorProfiler { //TODO (Detectors): Implement profiling in the new system

    private final EventSystem eventSystem;

    public DetectorProfiler(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

}
