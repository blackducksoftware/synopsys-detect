/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pip;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class PipInspectorDetectableOptions {
    private final String pipProjectName;
    private final List<Path> requirementsFilePaths;

    public PipInspectorDetectableOptions(final String pipProjectName, final List<Path> requirementsFilePaths) {
        this.pipProjectName = pipProjectName;
        this.requirementsFilePaths = requirementsFilePaths;
    }

    public Optional<String> getPipProjectName() {
        return Optional.ofNullable(pipProjectName);
    }

    public List<Path> getRequirementsFilePaths() {
        return requirementsFilePaths;
    }
}
