package com.synopsys.integration.detectable.detectable.result;

public class ExecutableNotFoundDetectableResult extends FailedDetectableResult {
    private final String executableName;

    public ExecutableNotFoundDetectableResult(String executableName) {
        this.executableName = executableName;
    }

    @Override
    public String toDescription() {
        return "No " + executableName + " executable was found.";
    }
}
