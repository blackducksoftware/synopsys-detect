package com.synopsys.integration.detectable.detectables.lerna;

import java.util.List;

public class LernaOptions {
    private final boolean includePrivatePackages;
    private List<String> excludedPackages;
    private List<String> includedPackages;

    public LernaOptions(boolean includePrivatePackages, List<String> excludedPackages, List<String> includedPackages) {
        this.includePrivatePackages = includePrivatePackages;
        this.excludedPackages = excludedPackages;
        this.includedPackages = includedPackages;
    }

    public boolean shouldIncludePrivatePackages() {
        return includePrivatePackages;
    }

    public List<String> getExcludedPackages() {
        return excludedPackages;
    }

    public List<String> getIncludedPackages() {
        return includedPackages;
    }
}
