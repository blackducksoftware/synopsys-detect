package com.blackducksoftware.integration.hub.detect.workflow.shutdown;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.event.Event;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;

public class ExitCodeManager {
    private List<ExitCodeRequest> exitCodeRequests = new ArrayList<>();
    private ExitCodeUtility exitCodeUtility;

    public ExitCodeManager(EventSystem eventSystem, ExitCodeUtility exitCodeUtility) {
        this.exitCodeUtility = exitCodeUtility;
        eventSystem.registerListener(Event.ExitCode, event -> addExitCodeRequest((ExitCodeRequest) event));
    }

    public void requestExitCode(Exception e) {
        requestExitCode(exitCodeUtility.getExitCodeFromExceptionDetails(e));
    }

    public void requestExitCode(ExitCodeType exitCodeType) {
        exitCodeRequests.add(new ExitCodeRequest(exitCodeType));
    }

    public void addExitCodeRequest(ExitCodeRequest request) {
        exitCodeRequests.add(request);
    }

    public ExitCodeType getWinningExitCode() {
        ExitCodeType winningExitCodeType = ExitCodeType.SUCCESS;
        for (ExitCodeRequest exitCodeRequest : exitCodeRequests) {
            winningExitCodeType = ExitCodeType.getWinningExitCodeType(winningExitCodeType, exitCodeRequest.getExitCodeType());
        }
        return winningExitCodeType;
    }
}
