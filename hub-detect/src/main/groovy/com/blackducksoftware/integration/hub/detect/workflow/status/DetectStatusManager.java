package com.blackducksoftware.integration.hub.detect.workflow.status;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.event.Event;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.synopsys.integration.log.IntLogger;

public class DetectStatusManager {
    private List<Status> statusSummaries = new ArrayList<>();

    public DetectStatusManager(EventSystem eventSystem) {
        eventSystem.registerListener(Event.StatusSummary, event -> addStatusSummary((Status) event));
    }

    public void addStatusSummary(Status status) {
        statusSummaries.add(status);
    }

    public void logDetectResults(final IntLogger logger, final ExitCodeType exitCodeType) {
        new DetectStatusLogger().logDetectResults(logger, statusSummaries, exitCodeType);
    }
}
