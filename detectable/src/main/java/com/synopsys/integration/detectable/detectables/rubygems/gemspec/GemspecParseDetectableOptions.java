package com.synopsys.integration.detectable.detectables.rubygems.gemspec;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.rubygems.GemspecDependencyType;

public class GemspecParseDetectableOptions {
    private final EnumListFilter<GemspecDependencyType> dependencyTypeFilter;

    public GemspecParseDetectableOptions(EnumListFilter<GemspecDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<GemspecDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
