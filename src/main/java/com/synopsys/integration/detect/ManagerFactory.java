package com.synopsys.integration.detect;

import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeUtility;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutputManager;
import com.synopsys.integration.detect.workflow.status.DetectStatusManager;

public class ManagerFactory {
    private final EventSystem eventSystem;
    private final ExitCodeUtility exitCodeUtility;

    public ManagerFactory(EventSystem eventSystem, ExitCodeUtility exitCodeUtility) {
        this.eventSystem = eventSystem;
        this.exitCodeUtility = exitCodeUtility;
    }

    public FormattedOutputManager createFormattedOutputManager() {
        return new FormattedOutputManager(eventSystem);
    }

    public DetectStatusManager createDetectStatusManager() {
        return new DetectStatusManager(eventSystem);
    }

    public ExitCodeManager createExitCodeManager() {
        return new ExitCodeManager(eventSystem, exitCodeUtility);
    }

}
