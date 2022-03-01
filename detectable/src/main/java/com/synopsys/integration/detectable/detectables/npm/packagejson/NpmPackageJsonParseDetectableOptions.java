package com.synopsys.integration.detectable.detectables.npm.packagejson;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;

public class NpmPackageJsonParseDetectableOptions {
    private final EnumListFilter<NpmDependencyType> npmDependencyTypeFilter;

    public NpmPackageJsonParseDetectableOptions(EnumListFilter<NpmDependencyType> npmDependencyTypeFilter) {
        this.npmDependencyTypeFilter = npmDependencyTypeFilter;
    }

    public EnumListFilter<NpmDependencyType> getNpmDependencyTypeFilter() {
        return npmDependencyTypeFilter;
    }
}
