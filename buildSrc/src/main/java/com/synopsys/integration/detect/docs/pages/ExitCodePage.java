/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.pages;

import java.util.List;

import com.synopsys.integration.detect.docs.copied.HelpJsonExitCode;

public class ExitCodePage {
    private final List<HelpJsonExitCode> exitCodes;

    public ExitCodePage(final List<HelpJsonExitCode> exitCodes) {
        this.exitCodes = exitCodes;
    }

    public List<HelpJsonExitCode> getExitCodes() {
        return exitCodes;
    }
} 
