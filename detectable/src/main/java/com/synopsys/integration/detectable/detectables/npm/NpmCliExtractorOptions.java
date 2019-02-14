package com.synopsys.integration.detectable.detectables.npm;

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

    public String getNpmArguments() {
        return npmArguments;
    }
}
