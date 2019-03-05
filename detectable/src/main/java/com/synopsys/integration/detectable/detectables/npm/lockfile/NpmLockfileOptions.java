package com.synopsys.integration.detectable.detectables.npm.lockfile;

public class NpmLockfileOptions {
    final boolean includeDeveloperDependencies;

    public NpmLockfileOptions(final boolean includeDeveloperDependencies) {
        this.includeDeveloperDependencies = includeDeveloperDependencies;
    }

    public boolean shouldIncludeDeveloperDependencies() {
        return includeDeveloperDependencies;
    }
}
