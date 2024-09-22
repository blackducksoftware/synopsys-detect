package com.blackduck.integration.detectable.detectables.npm.lockfile;

import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.npm.NpmDependencyType;

// TODO: Identical to NpmPackageJsonParseDetectableOptions. Similar to NpmCliExtractorOptions. Common base Options class? JM-01/2022
public class NpmLockfileOptions {
    private final EnumListFilter<NpmDependencyType> npmDependencyTypeFilter;

    public NpmLockfileOptions(EnumListFilter<NpmDependencyType> npmDependencyTypeFilter) {
        this.npmDependencyTypeFilter = npmDependencyTypeFilter;
    }

    public EnumListFilter<NpmDependencyType> getNpmDependencyTypeFilter() {
        return npmDependencyTypeFilter;
    }
}
