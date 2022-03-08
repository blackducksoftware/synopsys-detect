package com.synopsys.integration.detect.lifecycle.shutdown;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;

public class ExitCodeRequest {
    private final ExitCodeType exitCodeType;
    private final String reason;

    public ExitCodeRequest(ExitCodeType exitCodeType, String reason) {
        this.exitCodeType = exitCodeType;
        this.reason = reason;
    }

    public ExitCodeRequest(ExitCodeType exitCodeType) {
        this(exitCodeType, null);
    }

    public ExitCodeType getExitCodeType() {
        return exitCodeType;
    }

    public String getReason() {
        return reason;
    }
}
