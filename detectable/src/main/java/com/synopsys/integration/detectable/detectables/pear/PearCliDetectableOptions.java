package com.synopsys.integration.detectable.detectables.pear;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;

public class PearCliDetectableOptions {
    private final EnumListFilter<PearDependencyType> dependencyTypeFilter;

    public PearCliDetectableOptions(EnumListFilter<PearDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<PearDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
