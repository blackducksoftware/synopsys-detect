/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;

public class DetectUserFriendlyException extends Exception {
    private static final long serialVersionUID = 1L;

    private final ExitCodeType exitCodeType;

    public DetectUserFriendlyException(final String message, final ExitCodeType exitCodeType) {
        super(message);
        this.exitCodeType = exitCodeType;
    }

    public DetectUserFriendlyException(final String message, final Throwable cause, final ExitCodeType exitCodeType) {
        super(message, cause);
        this.exitCodeType = exitCodeType;
    }

    public ExitCodeType getExitCodeType() {
        return exitCodeType;
    }

}
