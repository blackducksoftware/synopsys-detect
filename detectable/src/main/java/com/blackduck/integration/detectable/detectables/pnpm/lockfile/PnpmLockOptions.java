package com.blackduck.integration.detectable.detectables.pnpm.lockfile;

import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyType;

public class PnpmLockOptions {
    private final EnumListFilter<PnpmDependencyType> dependencyTypeFilter;

    public PnpmLockOptions(EnumListFilter<PnpmDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public EnumListFilter<PnpmDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
