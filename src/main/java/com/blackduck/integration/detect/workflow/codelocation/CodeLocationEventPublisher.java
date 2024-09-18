package com.blackduck.integration.detect.workflow.codelocation;

import java.util.Collection;

import com.blackduck.integration.detect.workflow.event.Event;
import com.blackduck.integration.detect.workflow.event.EventSystem;
import com.blackduck.integration.detect.workflow.status.FormattedCodeLocation;

public class CodeLocationEventPublisher {
    private final EventSystem eventSystem;

    public CodeLocationEventPublisher(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    public void publishCodeLocationsCompleted(Collection<FormattedCodeLocation> codeLocationDataCollection) {
        eventSystem.publishEvent(Event.CodeLocationsCompleted, codeLocationDataCollection);
    }

    public void publishDetectCodeLocationNamesCalculated(DetectCodeLocationNamesResult detectCodeLocationNamesResult) {
        eventSystem.publishEvent(Event.DetectCodeLocationNamesCalculated, detectCodeLocationNamesResult);
    }
}
