/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

import java.util.Arrays;
import java.util.List;

public class FilesNotFoundDetectableResult extends FailedDetectableResult {
    private final List<String> patterns;

    public FilesNotFoundDetectableResult(final String... patterns) {
        this.patterns = Arrays.asList(patterns);
    }

    public FilesNotFoundDetectableResult(final List<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public String toDescription() {
        return "No files were found with any of the patterns: " + String.join(",", patterns);
    }
}
