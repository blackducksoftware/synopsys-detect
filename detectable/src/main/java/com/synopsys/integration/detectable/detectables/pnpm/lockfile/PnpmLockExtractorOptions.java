package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

public class PnpmLockExtractorOptions {
    private boolean includeDevDependencies;
    private boolean includeOptionalDependencies;

    public PnpmLockExtractorOptions(boolean includeDevDependencies, boolean includeOptionalDependencies) {
        this.includeDevDependencies = includeDevDependencies;
        this.includeOptionalDependencies = includeOptionalDependencies;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public boolean shouldIncludeOptionalDependencies() {
        return includeOptionalDependencies;
    }
}
