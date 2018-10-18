package com.blackducksoftware.integration.hub.detect.workflow.exit;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.event.Event;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;

public class ExitCodeManager {
    private List<ExitCodeRequest> exitCodeRequests = new ArrayList<>();

    public ExitCodeManager(EventSystem eventSystem) {
        eventSystem.registerListener(Event.ExitCode, event -> addExitCodeRequest((ExitCodeRequest) event));
    }

    public void addExitCodeRequest(ExitCodeRequest request) {
        exitCodeRequests.add(request);
    }

    public ExitCodeType getWinningExitCode(final ExitCodeType... additionalExitCodes) {
        return ExitCodeType.SUCCESS;
    }
}
