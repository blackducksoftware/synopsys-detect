package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;

public class DetectUserFriendlyException extends Exception {
    private static final long serialVersionUID = 1L;

    private final ExitCodeType exitCodeType;

    public DetectUserFriendlyException(String message, ExitCodeType exitCodeType) {
        super(message);
        this.exitCodeType = exitCodeType;
    }

    public DetectUserFriendlyException(String message, Throwable cause, ExitCodeType exitCodeType) {
        super(message, cause);
        this.exitCodeType = exitCodeType;
    }

    public ExitCodeType getExitCodeType() {
        return exitCodeType;
    }

}
