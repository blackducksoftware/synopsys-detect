package com.synopsys.integration.detectable.detectables.npm.lockfile;

public class NpmLockfileOptions {
    final boolean includeDeveloperDependencies;
    final boolean includePeerDependencies;

    public NpmLockfileOptions(boolean includeDeveloperDependencies, boolean includePeerDependencies) {
        this.includeDeveloperDependencies = includeDeveloperDependencies;
        this.includePeerDependencies = includePeerDependencies;
    }

    public boolean shouldIncludeDeveloperDependencies() {
        return includeDeveloperDependencies;
    }

    public boolean shouldIncludePeerDependencies() {
        return includePeerDependencies;
    }
}
