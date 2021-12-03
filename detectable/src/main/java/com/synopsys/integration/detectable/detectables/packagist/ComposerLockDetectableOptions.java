package com.synopsys.integration.detectable.detectables.packagist;

public class ComposerLockDetectableOptions {
    private final boolean includeDevDependencies;

    public ComposerLockDetectableOptions(boolean includeDevDependencies) {
        this.includeDevDependencies = includeDevDependencies;
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }
}
