package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

public class PnpmLockExtractorOptions {
    private boolean includeDevDependencies;

    public PnpmLockExtractorOptions(boolean includeDevDependencies) {
        this.includeDevDependencies = includeDevDependencies;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }
}
