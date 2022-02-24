package com.synopsys.integration.detectable.detectables.lerna;

import java.util.List;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;

public class LernaOptions {
    private final EnumListFilter<LernaPackageType> lernaPackageTypeFilter;
    private final List<String> excludedPackages;
    private final List<String> includedPackages;

    public LernaOptions(EnumListFilter<LernaPackageType> lernaPackageTypeFilter, List<String> excludedPackages, List<String> includedPackages) {
        this.lernaPackageTypeFilter = lernaPackageTypeFilter;
        this.excludedPackages = excludedPackages;
        this.includedPackages = includedPackages;
    }

    public EnumListFilter<LernaPackageType> getLernaPackageTypeFilter() {
        return lernaPackageTypeFilter;
    }

    public List<String> getExcludedPackages() {
        return excludedPackages;
    }

    public List<String> getIncludedPackages() {
        return includedPackages;
    }
}
