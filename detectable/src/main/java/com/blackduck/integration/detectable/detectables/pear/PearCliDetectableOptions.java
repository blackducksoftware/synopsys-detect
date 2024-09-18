package com.blackduck.integration.detectable.detectables.pear;

import com.blackduck.integration.detectable.detectable.util.EnumListFilter;

public class PearCliDetectableOptions {
    private final EnumListFilter<PearDependencyType> dependencyTypeFilter;

    public PearCliDetectableOptions(EnumListFilter<PearDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<PearDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
