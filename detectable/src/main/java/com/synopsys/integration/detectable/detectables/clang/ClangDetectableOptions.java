/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang;

public class ClangDetectableOptions {
    private final boolean cleanup;

    public ClangDetectableOptions(final boolean cleanup) {
        this.cleanup = cleanup;
    }

    public boolean isCleanup() {
        return cleanup;
    }
}
