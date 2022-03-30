package com.synopsys.integration.detectable.detectable.result;

public class PipfileLockNotFoundDetectableResult extends FailedDetectableResult {
    private final String directoryPath;

    public PipfileLockNotFoundDetectableResult(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toDescription() {
        return String.format("A Pipfile.lock file was NOT found in %s. Please run 'pipenv lock' in that location and try again.", directoryPath);
    }
}
