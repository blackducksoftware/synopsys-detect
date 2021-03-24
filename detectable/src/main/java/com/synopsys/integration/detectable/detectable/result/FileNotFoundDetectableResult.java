/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class FileNotFoundDetectableResult extends FailedDetectableResult {
    private final String pattern;

    public FileNotFoundDetectableResult(final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toDescription() {
        return "No file was found with pattern: " + pattern;
    }
}
