package com.synopsys.integration.detectable.detectables.bitbake;

import java.util.List;

public class BitbakeDetectableOptions {
    private final String buildEnvName;
    private final List<String> sourceArguments;
    private final List<String> packageNames;
    private final Integer searchDepth;
    private final boolean followSymLinks;
    private final boolean includeDevDependencies;

    public BitbakeDetectableOptions(String buildEnvName, List<String> sourceArguments, List<String> packageNames, Integer searchDepth, boolean followSymLinks, boolean includeDevDependencies) {
        this.buildEnvName = buildEnvName;
        this.sourceArguments = sourceArguments;
        this.packageNames = packageNames;
        this.searchDepth = searchDepth;
        this.followSymLinks = followSymLinks;
        this.includeDevDependencies = includeDevDependencies;
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

    public boolean isIncludeDevDependencies() {
        return includeDevDependencies;
    }
}
