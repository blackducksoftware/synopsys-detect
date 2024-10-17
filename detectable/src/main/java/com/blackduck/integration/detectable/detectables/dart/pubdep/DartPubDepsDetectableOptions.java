package com.blackduck.integration.detectable.detectables.dart.pubdep;

import com.blackduck.integration.detectable.detectable.util.EnumListFilter;

public class DartPubDepsDetectableOptions {
    private final EnumListFilter<DartPubDependencyType> dependencyTypeFilter;

    public DartPubDepsDetectableOptions(EnumListFilter<DartPubDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<DartPubDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
