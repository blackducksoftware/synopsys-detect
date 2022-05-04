package com.synopsys.integration.detect.workflow.bdio;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class BdioOptions {
    private final boolean enabledBdio2;
    @Nullable
    private final String projectCodeLocationSuffix;
    @Nullable
    private final String projectCodeLocationPrefix;
    @Nullable
    private final String bdioFileName;

    public BdioOptions(boolean enabledBdio2, @Nullable String projectCodeLocationPrefix, @Nullable String projectCodeLocationSuffix, @Nullable String bdioFileName) {
        this.enabledBdio2 = enabledBdio2;
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

    @Deprecated
    public boolean isBdio2Enabled() {
        return enabledBdio2;
    }

    public Optional<String> getBdioFileName() {
        return Optional.ofNullable(bdioFileName);
    }
}
