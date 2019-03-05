package com.synopsys.integration.detectable.detectables.rubygems.gemspec;

public class GemspecParseDetectableOptions {
    final boolean includeRuntimeDependencies;
    final boolean includeDevelopmentDependencies;

    public GemspecParseDetectableOptions(final boolean includeRuntimeDependencies, final boolean includeDevelopmentDependencies) {
        this.includeRuntimeDependencies = includeRuntimeDependencies;
        this.includeDevelopmentDependencies = includeDevelopmentDependencies;
    }

    public boolean shouldIncludeRuntimeDependencies() {
        return includeRuntimeDependencies;
    }

    public boolean shouldIncludeDevelopmentDependencies() {
        return includeDevelopmentDependencies;
    }
}
