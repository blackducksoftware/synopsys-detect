package com.synopsys.integration.detectable.detectables.pipenv.parse;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;

public class PipfileLockDetectableOptions {
    private final EnumListFilter<PipenvDependencyType> dependencyTypeFilter;

    public PipfileLockDetectableOptions(EnumListFilter<PipenvDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<PipenvDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
