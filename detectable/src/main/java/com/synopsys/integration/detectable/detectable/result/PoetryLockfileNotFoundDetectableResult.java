package com.synopsys.integration.detectable.detectable.result;

public class PoetryLockfileNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A pyproject.toml was located in %s, but the Poetry.lock file was NOT located. Please run 'poetry install' in that location and try again.";

    public PoetryLockfileNotFoundDetectableResult(String directoryPath) {
        super(String.format(FORMAT, directoryPath));
    }
}
