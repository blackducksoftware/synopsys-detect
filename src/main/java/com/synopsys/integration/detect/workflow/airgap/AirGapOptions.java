/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.airgap;

import java.nio.file.Path;
import java.util.Optional;

public class AirGapOptions {
    private final Path dockerInspectorPathOverride;
    private final Path gradleInspectorPathOverride;
    private final Path nugetInspectorPathOverride;
    private final Path fontsPathOverride;

    public AirGapOptions(Path dockerInspectorPathOverride, Path gradleInspectorPathOverride, Path nugetInspectorPathOverride, Path fontsPathOverride) {
        this.dockerInspectorPathOverride = dockerInspectorPathOverride;
        this.gradleInspectorPathOverride = gradleInspectorPathOverride;
        this.nugetInspectorPathOverride = nugetInspectorPathOverride;
        this.fontsPathOverride = fontsPathOverride;
    }

    public Optional<Path> getDockerInspectorPathOverride() {
        return Optional.ofNullable(dockerInspectorPathOverride);
    }

    public Optional<Path> getGradleInspectorPathOverride() {
        return Optional.ofNullable(gradleInspectorPathOverride);
    }

    public Optional<Path> getNugetInspectorPathOverride() {
        return Optional.ofNullable(nugetInspectorPathOverride);
    }

    public Optional<Path> getFontsPathOverride() {
        return Optional.ofNullable(fontsPathOverride);
    }
}
