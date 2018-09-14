package com.blackducksoftware.integration.hub.detect.workflow;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.summary.StatusSummary;

public class DetectRunReport {
    private final List<ExitCodeType> requestedExitCodes = new ArrayList<>();
    private final List<StatusSummary> statusSummaries = new ArrayList<>();

    public  DetectRunReport() {

    }

    public void requestExitCode(ExitCodeType exitCodeType) {
        requestedExitCodes.add(exitCodeType);
    }

    public void reportStatus(StatusSummary statusSummary){
        statusSummaries.add(statusSummary);
    }
}
