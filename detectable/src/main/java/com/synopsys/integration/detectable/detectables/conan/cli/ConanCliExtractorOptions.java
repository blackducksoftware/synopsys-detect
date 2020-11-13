package com.synopsys.integration.detectable.detectables.conan.cli;

public class ConanCliExtractorOptions {
    private final boolean includeDevDependencies;

    public ConanCliExtractorOptions(boolean includeDevDependencies) {
        this.includeDevDependencies = includeDevDependencies;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }
}
