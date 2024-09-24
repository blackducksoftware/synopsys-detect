package com.blackduck.integration.detectable.detectables.packagist;

import com.blackduck.integration.detectable.detectable.util.EnumListFilter;

public class ComposerLockDetectableOptions {
    private final EnumListFilter<PackagistDependencyType> dependencyTypeFilter;

    public ComposerLockDetectableOptions(EnumListFilter<PackagistDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<PackagistDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
