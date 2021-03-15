/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pip;

import java.util.Optional;

public class PipenvDetectableOptions {
    private final String pipProjectName;
    private final String pipProjectVersionName;
    private final boolean pipProjectTreeOnly;

    public PipenvDetectableOptions(final String pipProjectName, final String pipProjectVersionName, final boolean pipProjectTreeOnly) {
        this.pipProjectName = pipProjectName;
        this.pipProjectVersionName = pipProjectVersionName;
        this.pipProjectTreeOnly = pipProjectTreeOnly;
    }

    public Optional<String> getPipProjectName() {
        return Optional.ofNullable(pipProjectName);
    }

    public Optional<String> getPipProjectVersionName() {
        return Optional.ofNullable(pipProjectVersionName);
    }

    public boolean isPipProjectTreeOnly() {
        return pipProjectTreeOnly;
    }
}
