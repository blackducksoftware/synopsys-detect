package com.synopsys.integration.detectable.detectables.conan.cli.config;

import java.nio.file.Path;
import java.util.Optional;

import com.synopsys.integration.detectable.detectable.util.ExcludedDependencyTypeFilter;

public class ConanCliOptions {
    private final Path lockfilePath;
    private final String additionalArguments;
    private final ExcludedDependencyTypeFilter<ConanDependencyType> dependencyTypeFilter;
    private final boolean preferLongFormExternalIds;

    public ConanCliOptions(Path lockfilePath, String additionalArguments, ExcludedDependencyTypeFilter<ConanDependencyType> dependencyTypeFilter, boolean preferLongFormExternalIds) {
        this.lockfilePath = lockfilePath;
        this.additionalArguments = additionalArguments;
        this.dependencyTypeFilter = dependencyTypeFilter;
        this.preferLongFormExternalIds = preferLongFormExternalIds;
    }

    public Optional<Path> getLockfilePath() {
        return Optional.ofNullable(lockfilePath);
    }

    public Optional<String> getAdditionalArguments() {
        return Optional.ofNullable(additionalArguments);
    }

    public ExcludedDependencyTypeFilter<ConanDependencyType> getDependencyTypeFilter() {
        return dependencyTypeFilter;
    }

    public boolean preferLongFormExternalIds() {
        return preferLongFormExternalIds;
    }
}
