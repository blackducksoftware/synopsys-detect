/*
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
    private final boolean includePeerDependencies;
    private final String npmArguments;

    public NpmCliExtractorOptions(boolean includeDevDependencies, boolean includePeerDependencies, String npmArguments) {
        this.includeDevDependencies = includeDevDependencies;
        this.includePeerDependencies = includePeerDependencies;
        this.npmArguments = npmArguments;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public boolean shouldIncludePeerDependencies() {
        return includePeerDependencies;
    }

    public Optional<String> getNpmArguments() {
        return Optional.ofNullable(npmArguments);
    }
}
