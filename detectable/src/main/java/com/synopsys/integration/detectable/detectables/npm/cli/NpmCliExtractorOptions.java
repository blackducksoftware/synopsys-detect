/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.cli;

import java.util.Optional;

public class NpmCliExtractorOptions {
    private final boolean includeDevDependencies;
    private final String npmArguments;

    public NpmCliExtractorOptions(final boolean includeDevDependencies, final String npmArguments) {
        this.includeDevDependencies = includeDevDependencies;
        this.npmArguments = npmArguments;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public Optional<String> getNpmArguments() {
        return Optional.ofNullable(npmArguments);
    }
}
