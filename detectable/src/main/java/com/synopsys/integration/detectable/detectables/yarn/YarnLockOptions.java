/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn;

import java.util.List;

public class YarnLockOptions {
    private final boolean useProductionOnly;
    // By default, detector will follow declared dependencies, which may lead it to include some or all workspaces
    // To override that behavior, to force workspaces out or in, use:
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
