package com.synopsys.integration.detectable.detectables.pip.parser;

import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;

public class RequirementsFileNotFoundDetectableResult extends FailedDetectableResult {
    private final String directoryPath;

    public RequirementsFileNotFoundDetectableResult(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toDescription() {
        return String.format("A PIP requirements file (example: requirements.txt) was NOT found in %s. Please ensure a requirements file is present at that location and try again.", directoryPath);
    }
}
