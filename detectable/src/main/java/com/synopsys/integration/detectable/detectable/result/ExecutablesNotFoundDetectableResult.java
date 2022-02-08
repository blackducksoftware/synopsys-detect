package com.synopsys.integration.detectable.detectable.result;

import java.util.List;

public class ExecutablesNotFoundDetectableResult extends FailedDetectableResult {
    private final List<String> executableNames;

    public ExecutablesNotFoundDetectableResult(List<String> executableNames) {
        this.executableNames = executableNames;
    }

    @Override
    public String toDescription() {
        return "None of the following executables were found " + String.join(",", executableNames);
    }
}
