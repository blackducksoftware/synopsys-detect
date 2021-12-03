package com.synopsys.integration.detect.workflow.codelocation;

import java.util.Collection;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class CodeLocationEventPublisher {
    private final EventSystem eventSystem;

    public CodeLocationEventPublisher(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    public void publishCodeLocationsCompleted(Collection<String> codeLocationNameCollection) {
        eventSystem.publishEvent(Event.CodeLocationsCompleted, codeLocationNameCollection);
    }

    public void publishDetectCodeLocationNamesCalculated(DetectCodeLocationNamesResult detectCodeLocationNamesResult) {
        eventSystem.publishEvent(Event.DetectCodeLocationNamesCalculated, detectCodeLocationNamesResult);
    }
}
