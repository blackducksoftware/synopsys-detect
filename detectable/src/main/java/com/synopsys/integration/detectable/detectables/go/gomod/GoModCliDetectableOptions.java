package com.synopsys.integration.detectable.detectables.go.gomod;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;

public class GoModCliDetectableOptions {
    private final EnumListFilter<GoModDependencyType> dependencyTypeFilter;

    public GoModCliDetectableOptions(EnumListFilter<GoModDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<GoModDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
