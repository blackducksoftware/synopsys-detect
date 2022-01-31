package com.synopsys.integration.detectable.detectables.npm.cli;

import java.util.Optional;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;

public class NpmCliExtractorOptions {
    private final EnumListFilter<NpmDependencyType> npmDependencyTypeFilter;
    private final String npmArguments;

    public NpmCliExtractorOptions(EnumListFilter<NpmDependencyType> npmDependencyTypeFilter, String npmArguments) {
        this.npmDependencyTypeFilter = npmDependencyTypeFilter;
        this.npmArguments = npmArguments;
    }

    public EnumListFilter<NpmDependencyType> getDependencyTypeFilter() {
        return npmDependencyTypeFilter;
    }

    public Optional<String> getNpmArguments() {
        return Optional.ofNullable(npmArguments);
    }
}
