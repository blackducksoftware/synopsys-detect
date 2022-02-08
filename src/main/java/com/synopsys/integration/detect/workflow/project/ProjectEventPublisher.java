package com.synopsys.integration.detect.workflow.project;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.util.NameVersion;

public class ProjectEventPublisher {
    private final EventSystem eventSystem;

    public ProjectEventPublisher(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    public void publishProjectNameVersionChosen(NameVersion projectNameVersion) {
        eventSystem.publishEvent(Event.ProjectNameVersionChosen, projectNameVersion);
    }
}
