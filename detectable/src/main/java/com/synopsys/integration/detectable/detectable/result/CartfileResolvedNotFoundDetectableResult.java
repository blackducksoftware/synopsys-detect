package com.synopsys.integration.detectable.detectable.result;

public class CartfileResolvedNotFoundDetectableResult extends FailedDetectableResult {
    private static final String FORMAT = "A Cartfile.resolved file was NOT found in %s. Please run 'carthage update' in that location and try again.";

    public CartfileResolvedNotFoundDetectableResult(String directoryPath) {
        super(String.format(FORMAT, directoryPath));
    }
}
