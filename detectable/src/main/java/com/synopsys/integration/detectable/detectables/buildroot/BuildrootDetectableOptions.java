package com.synopsys.integration.detectable.detectables.buildroot;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;

public class BuildrootDetectableOptions {
    private final EnumListFilter<BuildrootDependencyType> dependencyTypeFilter;

    public BuildrootDetectableOptions(EnumListFilter<BuildrootDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<BuildrootDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
