/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class GivenFileNotFoundDetectableResult extends FailedDetectableResult {
    private final String givenFilePath;

    public GivenFileNotFoundDetectableResult(String givenFilePath) {
        this.givenFilePath = givenFilePath;
    }

    @Override
    public String toDescription() {
        return String.format("The given file (%s) was not found or not readable", givenFilePath);
    }
}
