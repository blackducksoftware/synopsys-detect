package com.synopsys.integration.detect.lifecycle.exit;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.util.Stringable;

public class ExitResult extends Stringable {
    private final ExitCodeType exitCodeType;
    private final boolean forceSuccess;
    private final boolean performExit;

    public ExitResult(ExitCodeType exitCodeType, boolean forceSuccess, boolean performExit) {
        this.exitCodeType = exitCodeType;
        this.forceSuccess = forceSuccess;
        this.performExit = performExit;
    }

    public ExitCodeType getExitCodeType() {
        return exitCodeType;
    }

    public boolean shouldForceSuccess() {
        return forceSuccess;
    }

    public boolean shouldPerformExit() {
        return performExit;
    }
}
