package com.synopsys.integration.detectable.detectables.conan.lockfile;

import java.nio.file.Path;
import java.util.Optional;

import com.synopsys.integration.detectable.detectable.util.DependencyTypeFilter;
import com.synopsys.integration.detectable.detectables.conan.cli.config.ConanDependencyType;

public class ConanLockfileExtractorOptions {
    private final Path lockfilePath;
    private final DependencyTypeFilter<ConanDependencyType> dependencyTypeFilter;
    private final boolean preferLongFormExternalIds;

    public ConanLockfileExtractorOptions(Path lockfilePath, DependencyTypeFilter<ConanDependencyType> dependencyTypeFilter, boolean preferLongFormExternalIds) {
        this.lockfilePath = lockfilePath;
        this.dependencyTypeFilter = dependencyTypeFilter;
        this.preferLongFormExternalIds = preferLongFormExternalIds;
    }

    public Optional<Path> getLockfilePath() {
        return Optional.ofNullable(lockfilePath);
    }

    public DependencyTypeFilter<ConanDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }

    public boolean preferLongFormExternalIds() {
        return preferLongFormExternalIds;
    }
}
