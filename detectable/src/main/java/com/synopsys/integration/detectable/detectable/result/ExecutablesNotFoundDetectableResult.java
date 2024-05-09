package com.synopsys.integration.detectable.detectable.result;

import java.util.List;

public class ExecutablesNotFoundDetectableResult extends FailedDetectableResult {
    private static final String PREFIX = "None of the following executables were found ";

    public ExecutablesNotFoundDetectableResult(List<String> executableNames) {
        super(PREFIX, String.join(",", executableNames));
    }
}
