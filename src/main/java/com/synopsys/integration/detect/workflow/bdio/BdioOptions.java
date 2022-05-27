package com.synopsys.integration.detect.workflow.bdio;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class BdioOptions {
    @Nullable
    private final String projectCodeLocationSuffix;
    @Nullable
    private final String projectCodeLocationPrefix;
    @Nullable
    private final String bdioFileName;

    public BdioOptions( @Nullable String projectCodeLocationPrefix, @Nullable String projectCodeLocationSuffix, @Nullable String bdioFileName) {
        this.projectCodeLocationSuffix = projectCodeLocationSuffix;
        this.projectCodeLocationPrefix = projectCodeLocationPrefix;
        this.bdioFileName = bdioFileName;
    }

    public Optional<String> getProjectCodeLocationSuffix() {
        return Optional.ofNullable(projectCodeLocationSuffix);
    }

    public Optional<String> getProjectCodeLocationPrefix() {
        return Optional.ofNullable(projectCodeLocationPrefix);
    }

    public Optional<String> getBdioFileName() {
        return Optional.ofNullable(bdioFileName);
    }
}
