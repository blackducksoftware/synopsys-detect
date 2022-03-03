package com.synopsys.integration.detectable.detectable.result;

public class PoetryLockfileNotFoundDetectableResult extends FailedDetectableResult {
    private final String directoryPath;

    public PoetryLockfileNotFoundDetectableResult(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toDescription() {
        return String.format(
            "A pyproject.toml was located in %s, but the Poetry.lock file was NOT located. Please run 'poetry install' in that location and try again.",
            directoryPath
        );
    }
}
