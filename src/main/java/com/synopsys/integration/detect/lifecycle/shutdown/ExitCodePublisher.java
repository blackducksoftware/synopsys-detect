package com.synopsys.integration.detect.lifecycle.shutdown;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class ExitCodePublisher {
    private final EventSystem eventSystem;

    public ExitCodePublisher(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    public void publishExitCode(ExitCodeRequest exitCodeRequest) {
        eventSystem.publishEvent(Event.ExitCode, exitCodeRequest);
    }

    public void publishExitCode(ExitCodeType exitCodeType, String reason) {
        publishExitCode(new ExitCodeRequest(exitCodeType, reason));
    }
}
