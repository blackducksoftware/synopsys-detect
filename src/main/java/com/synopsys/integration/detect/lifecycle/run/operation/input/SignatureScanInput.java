/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.input;

import java.io.File;
import java.util.Optional;

import javax.annotation.Nullable;

import com.synopsys.integration.util.NameVersion;

public class SignatureScanInput {
    private final NameVersion projectNameVersion;
    private final File dockerTar;

    public SignatureScanInput(NameVersion projectNameVersion, @Nullable File dockerTar) {
        this.projectNameVersion = projectNameVersion;
        this.dockerTar = dockerTar;
    }

    public NameVersion getProjectNameVersion() {
        return projectNameVersion;
    }

    public Optional<File> getDockerTar() {
        return Optional.ofNullable(dockerTar);
    }
}
