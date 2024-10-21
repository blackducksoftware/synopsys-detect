package com.blackduck.integration.detectable.detectables.rubygems.gemspec;

import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.rubygems.GemspecDependencyType;

public class GemspecParseDetectableOptions {
    private final EnumListFilter<GemspecDependencyType> dependencyTypeFilter;

    public GemspecParseDetectableOptions(EnumListFilter<GemspecDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<GemspecDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
