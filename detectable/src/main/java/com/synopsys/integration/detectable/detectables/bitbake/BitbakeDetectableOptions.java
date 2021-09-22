/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bitbake;

import java.util.List;

public class BitbakeDetectableOptions {
    private final String buildEnvName;
    private final List<String> sourceArguments;
    private final List<String> packageNames;
    private final Integer searchDepth;
    private final boolean followSymLinks;

    public BitbakeDetectableOptions(String buildEnvName, List<String> sourceArguments, List<String> packageNames, Integer searchDepth, boolean followSymLinks) {
        this.buildEnvName = buildEnvName;
        this.sourceArguments = sourceArguments;
        this.packageNames = packageNames;
        this.searchDepth = searchDepth;
        this.followSymLinks = followSymLinks;
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
}
