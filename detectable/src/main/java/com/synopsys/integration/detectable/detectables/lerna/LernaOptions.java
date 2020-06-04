package com.synopsys.integration.detectable.detectables.lerna;

public class LernaOptions {
    private final boolean includePrivatePackages;

    public LernaOptions(boolean includePrivatePackages) {
        this.includePrivatePackages = includePrivatePackages;
    }

    public boolean shouldIncludePrivatePackages() {
        return includePrivatePackages;
    }
}
