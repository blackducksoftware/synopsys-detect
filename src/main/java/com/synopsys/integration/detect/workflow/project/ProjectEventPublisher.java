/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
