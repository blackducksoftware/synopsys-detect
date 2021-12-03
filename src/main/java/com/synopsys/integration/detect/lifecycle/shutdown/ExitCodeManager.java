package com.synopsys.integration.detect.lifecycle.shutdown;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class ExitCodeManager {
    private final List<ExitCodeRequest> exitCodeRequests = new ArrayList<>();
    private final ExitCodeUtility exitCodeUtility;

    public ExitCodeManager(EventSystem eventSystem, ExitCodeUtility exitCodeUtility) {
        this.exitCodeUtility = exitCodeUtility;
        eventSystem.registerListener(Event.ExitCode, this::addExitCodeRequest);
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

    public ExitCodeType getExitCodeFromExceptionDetails(Exception e) {
        return exitCodeUtility.getExitCodeFromExceptionDetails(e);
    }
}
