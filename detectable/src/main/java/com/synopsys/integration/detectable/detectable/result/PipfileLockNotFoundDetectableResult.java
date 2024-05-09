package com.synopsys.integration.detectable.detectable.result;

public class PipfileLockNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A Pipfile.lock file was NOT found in %s. Please run 'pipenv lock' in that location and try again.";

    public PipfileLockNotFoundDetectableResult(String directoryPath) {
        super(String.format(FORMAT, directoryPath));
    }
}
