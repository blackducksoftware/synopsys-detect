/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.impactanalysis;

import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

public class ImpactAnalysisOptions {
    private final Boolean enabled;
    @Nullable
    private final String codeLocationPrefix;
    @Nullable
    private final String codeLocationSuffix;
    @Nullable
    private final Path outputDirectory;

    public ImpactAnalysisOptions(Boolean enabled, @Nullable String codeLocationPrefix, @Nullable String codeLocationSuffix, @Nullable Path outputDirectory) {
        this.enabled = enabled;
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
        this.outputDirectory = outputDirectory;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    @Nullable
    public String getCodeLocationPrefix() {
        return codeLocationPrefix;
    }

    @Nullable
    public String getCodeLocationSuffix() {
        return codeLocationSuffix;
    }

    @Nullable
    public Path getOutputDirectory() {
        return outputDirectory;
    }
}
