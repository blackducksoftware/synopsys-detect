package com.synopsys.integration.detectable.detectables.bitbake;

import java.util.List;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;

public class BitbakeDetectableOptions {
    private final String buildEnvName;
    private final List<String> sourceArguments;
    private final List<String> packageNames;
    private final Integer searchDepth;
    private final boolean followSymLinks;
    private final EnumListFilter<BitbakeDependencyType> bitbakeDependencyTypeFilter;

    public BitbakeDetectableOptions(
        String buildEnvName,
        List<String> sourceArguments,
        List<String> packageNames,
        Integer searchDepth,
        boolean followSymLinks,
        EnumListFilter<BitbakeDependencyType> bitbakeDependencyTypeFilter
    ) {
        this.buildEnvName = buildEnvName;
        this.sourceArguments = sourceArguments;
        this.packageNames = packageNames;
        this.searchDepth = searchDepth;
        this.followSymLinks = followSymLinks;
        this.bitbakeDependencyTypeFilter = bitbakeDependencyTypeFilter;
    }

    public String getBuildEnvName() {
        return buildEnvName;
    }

    public List<String> getSourceArguments() {
        return sourceArguments;
    }

    public List<String> getPackageNames() {
        return packageNames;
    }

    public Integer getSearchDepth() {
        return searchDepth;
    }

    public boolean isFollowSymLinks() {
        return followSymLinks;
    }

    public EnumListFilter<BitbakeDependencyType> getDependencyTypeFilter() {
        return bitbakeDependencyTypeFilter;
    }
}
