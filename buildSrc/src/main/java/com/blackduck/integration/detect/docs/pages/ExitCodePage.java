package com.blackduck.integration.detect.docs.pages;

import java.util.List;

import com.blackduck.integration.detect.docs.copied.HelpJsonExitCode;

public class ExitCodePage {
    private final List<HelpJsonExitCode> exitCodes;

    public ExitCodePage(List<HelpJsonExitCode> exitCodes) {
        this.exitCodes = exitCodes;
    }

    public List<HelpJsonExitCode> getExitCodes() {
        return exitCodes;
    }
} 
