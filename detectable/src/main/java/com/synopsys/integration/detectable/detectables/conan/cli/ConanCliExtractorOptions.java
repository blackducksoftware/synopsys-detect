/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conan.cli;

import java.nio.file.Path;
import java.util.Optional;

public class ConanCliExtractorOptions {
    private final Path lockfilePath;
    private final String additionalArguments;
    private final boolean includeDevDependencies;
    private final boolean preferLongFormExternalIds;

    public ConanCliExtractorOptions(Path lockfilePath, String additionalArguments, boolean includeDevDependencies,
        boolean preferLongFormExternalIds) {
        this.lockfilePath = lockfilePath;
        this.additionalArguments = additionalArguments;
        this.includeDevDependencies = includeDevDependencies;
        this.preferLongFormExternalIds = preferLongFormExternalIds;
    }

    public Optional<Path> getLockfilePath() {
        return Optional.ofNullable(lockfilePath);
    }

    public Optional<String> getAdditionalArguments() {
        return Optional.ofNullable(additionalArguments);
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public boolean preferLongFormExternalIds() {
        return preferLongFormExternalIds;
    }
}
