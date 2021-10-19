package com.synopsys.integration.detectable.detectables.pnpm.cli;

import java.util.Optional;

public class PnpmCliExtractorOptions {
    private final boolean includeDevDependencies;
    private final String pnpmArguments;

    public PnpmCliExtractorOptions(boolean includeDevDependencies, String pnpmArguments) {
        this.includeDevDependencies = includeDevDependencies;
        this.pnpmArguments = pnpmArguments;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public Optional<String> getPnpmArguments() {
        return Optional.ofNullable(pnpmArguments);
    }

}
