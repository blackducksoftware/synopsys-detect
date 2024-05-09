package com.synopsys.integration.detectable.detectable.result;

public class ExecutableNotFoundDetectableResult extends FailedDetectableResult {
    private static final String PREFIX = "No ";
    private static final String SUFFIX = " executable was found.";

    public ExecutableNotFoundDetectableResult(String executableName) {
        super(PREFIX, executableName, SUFFIX);
    }
}
