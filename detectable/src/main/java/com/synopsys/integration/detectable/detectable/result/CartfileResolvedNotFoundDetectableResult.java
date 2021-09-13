/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class CartfileResolvedNotFoundDetectableResult extends FailedDetectableResult {
    private final String directoryPath;

    public CartfileResolvedNotFoundDetectableResult(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public String toDescription() {
        return String.format("A Cartfile was located in %s, but the Cartfile.resolved file was NOT located. Please run 'carthage update' in that location and try again.", directoryPath);
    }
}
