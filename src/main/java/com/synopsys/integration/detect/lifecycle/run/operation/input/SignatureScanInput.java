/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.input;

import java.util.Optional;

import javax.annotation.Nullable;

import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.util.NameVersion;

public class SignatureScanInput {
    private final NameVersion projectNameVersion;
    private final DockerTargetData dockerTargetData;

    public SignatureScanInput(NameVersion projectNameVersion, @Nullable DockerTargetData dockerTargetData) {
        this.projectNameVersion = projectNameVersion;
        this.dockerTargetData = dockerTargetData;
    }

    public NameVersion getProjectNameVersion() {
        return projectNameVersion;
    }

    public Optional<DockerTargetData> getDockerTargetData() {
        return Optional.ofNullable(dockerTargetData);
    }
}
