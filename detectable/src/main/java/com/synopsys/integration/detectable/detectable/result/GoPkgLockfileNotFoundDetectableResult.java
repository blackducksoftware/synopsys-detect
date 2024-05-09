package com.synopsys.integration.detectable.detectable.result;

public class GoPkgLockfileNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A Gopkg.toml was located in %s, but the Gopkg.lock file was NOT located. Please run 'go dep init' and 'go dep ensure' in that location and try again.";

    public GoPkgLockfileNotFoundDetectableResult(String directoryPath) {
        super(String.format( FORMAT, directoryPath));
    }
}