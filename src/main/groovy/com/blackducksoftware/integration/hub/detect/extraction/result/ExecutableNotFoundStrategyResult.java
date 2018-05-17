package com.blackducksoftware.integration.hub.detect.extraction.result;


public class ExecutableNotFoundStrategyResult extends FailedStrategyResult {
    private final String executableName;

    public ExecutableNotFoundStrategyResult(final String executableName) {
        this.executableName = executableName;
    }

    @Override
    public String toDescription() {
        return "No " + executableName + " executable was found.";
    }
}
