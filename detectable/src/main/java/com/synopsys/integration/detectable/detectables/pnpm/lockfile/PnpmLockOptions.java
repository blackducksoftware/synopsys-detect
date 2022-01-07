package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import com.synopsys.integration.detectable.detectable.util.DependencyTypeFilter;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.model.PnpmDependencyType;

public class PnpmLockOptions {
    private final DependencyTypeFilter<PnpmDependencyType> dependencyTypeFilter;

    public PnpmLockOptions(DependencyTypeFilter<PnpmDependencyType> dependencyTypeFilter) {
        this.dependencyTypeFilter = dependencyTypeFilter;
    }

    public DependencyTypeFilter<PnpmDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }
}
