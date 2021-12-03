package com.synopsys.integration.detectable.detectables.npm.packagejson;

public class NpmPackageJsonParseDetectableOptions {
    private final boolean includeDevDependencies;
    private final boolean includePeerDependencies;

    public NpmPackageJsonParseDetectableOptions(boolean includeDevDependencies, boolean includePeerDependencies) {
        this.includeDevDependencies = includeDevDependencies;
        this.includePeerDependencies = includePeerDependencies;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public boolean shouldIncludePeerDependencies() {
        return includePeerDependencies;
    }
}
