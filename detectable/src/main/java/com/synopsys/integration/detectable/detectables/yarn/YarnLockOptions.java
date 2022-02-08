package com.synopsys.integration.detectable.detectables.yarn;

import java.util.List;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;

public class YarnLockOptions {
    private final EnumListFilter<YarnDependencyType> yarnDependencyTypeFilter;
    private final List<String> excludedWorkspaceNamePatterns;
    private final List<String> includedWorkspaceNamePatterns;

    public YarnLockOptions(EnumListFilter<YarnDependencyType> yarnDependencyTypeFilter, List<String> excludedWorkspaceNamePatterns, List<String> includedWorkspaceNamePatterns) {
        this.yarnDependencyTypeFilter = yarnDependencyTypeFilter;
        this.excludedWorkspaceNamePatterns = excludedWorkspaceNamePatterns;
        this.includedWorkspaceNamePatterns = includedWorkspaceNamePatterns;
    }

    public EnumListFilter<YarnDependencyType> getYarnDependencyTypeFilter() {
        return yarnDependencyTypeFilter;
    }

    public List<String> getExcludedWorkspaceNamePatterns() {
        return excludedWorkspaceNamePatterns;
    }

    public List<String> getIncludedWorkspaceNamePatterns() {
        return includedWorkspaceNamePatterns;
    }
}
