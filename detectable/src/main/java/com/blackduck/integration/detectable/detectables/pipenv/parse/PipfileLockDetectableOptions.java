package com.blackduck.integration.detectable.detectables.pipenv.parse;

import com.blackduck.integration.detectable.detectable.util.EnumListFilter;

public class PipfileLockDetectableOptions {
    private final EnumListFilter<PipenvDependencyType> dependencyTypeFilter;

    public PipfileLockDetectableOptions(EnumListFilter<PipenvDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<PipenvDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
