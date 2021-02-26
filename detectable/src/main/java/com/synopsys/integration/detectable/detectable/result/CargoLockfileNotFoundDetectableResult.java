/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class CargoLockfileNotFoundDetectableResult extends FailedDetectableResult {
    private final String directoryPath;

    public CargoLockfileNotFoundDetectableResult(final String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toDescription() {
        return String.format("A Cargo.toml was located in %s, but the Cargo.lock file was NOT located. Please run 'cargo generate-lockfile' in that location and try again.", directoryPath);
    }
}
