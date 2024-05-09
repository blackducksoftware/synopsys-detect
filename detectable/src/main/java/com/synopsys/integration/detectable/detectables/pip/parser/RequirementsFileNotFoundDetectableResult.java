package com.synopsys.integration.detectable.detectables.pip.parser;

import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;

public class RequirementsFileNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A PIP requirements file (example: requirements.txt) was NOT found in %s. Please ensure a requirements file is present at that location and try again.";

    public RequirementsFileNotFoundDetectableResult(String directoryPath) {
        super(String.format(FORMAT, directoryPath));
    }
}
