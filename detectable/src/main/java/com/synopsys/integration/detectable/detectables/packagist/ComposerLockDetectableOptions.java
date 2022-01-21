package com.synopsys.integration.detectable.detectables.packagist;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;

public class ComposerLockDetectableOptions {
    private final EnumListFilter<PackagistDependencyType> dependencyTypeFilter;

    public ComposerLockDetectableOptions(EnumListFilter<PackagistDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<PackagistDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
