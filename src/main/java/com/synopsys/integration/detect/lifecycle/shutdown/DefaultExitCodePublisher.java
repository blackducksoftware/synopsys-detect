package com.synopsys.integration.detect.lifecycle.shutdown;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class DefaultExitCodePublisher implements ExitCodePublisher {
    private EventSystem eventSystem;

    public DefaultExitCodePublisher(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    @Override
    public void publishExitCode(ExitCodeRequest exitCodeRequest) {
        eventSystem.publishEvent(Event.ExitCode, exitCodeRequest);
    }
}
