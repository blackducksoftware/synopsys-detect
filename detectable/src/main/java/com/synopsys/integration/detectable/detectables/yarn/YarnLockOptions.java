package com.synopsys.integration.detectable.detectables.yarn;

import java.util.List;

public class YarnLockOptions {
    private final boolean useProductionOnly;
    private final List<String> excludedWorkspaceNamePatterns;
    private final List<String> includedWorkspaceNamePatterns;

    public YarnLockOptions(boolean useProductionOnly,
        List<String> excludedWorkspaceNamePatterns, List<String> includedWorkspaceNamePatterns) {
        this.useProductionOnly = useProductionOnly;
        this.excludedWorkspaceNamePatterns = excludedWorkspaceNamePatterns;
        this.includedWorkspaceNamePatterns = includedWorkspaceNamePatterns;
    }

    public boolean useProductionOnly() {
        return useProductionOnly;
    }

    public List<String> getExcludedWorkspaceNamePatterns() {
        return excludedWorkspaceNamePatterns;
    }

    public List<String> getIncludedWorkspaceNamePatterns() {
        return includedWorkspaceNamePatterns;
    }
}
