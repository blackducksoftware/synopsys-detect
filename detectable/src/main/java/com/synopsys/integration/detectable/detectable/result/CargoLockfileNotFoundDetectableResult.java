package com.synopsys.integration.detectable.detectable.result;

public class CargoLockfileNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A Cargo.toml was located in %s, but the Cargo.lock file was NOT located. Please run 'cargo generate-lockfile' in that location and try again.";
    
    public CargoLockfileNotFoundDetectableResult(String directoryPath) {
        super(String.format(FORMAT, directoryPath));
    }
}
