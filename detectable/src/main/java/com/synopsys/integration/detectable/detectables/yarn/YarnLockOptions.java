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
    private final boolean monoRepoMode;
    private final List<String> excludedWorkspaceNamePatterns;
    private final List<String> includedWorkspaceNamePatterns;

    public YarnLockOptions(boolean useProductionOnly, boolean monoRepoMode,
        List<String> excludedWorkspaceNamePatterns, List<String> includedWorkspaceNamePatterns) {
        this.useProductionOnly = useProductionOnly;
        this.monoRepoMode = monoRepoMode;
        this.excludedWorkspaceNamePatterns = excludedWorkspaceNamePatterns;
        this.includedWorkspaceNamePatterns = includedWorkspaceNamePatterns;
    }

    public boolean useProductionOnly() {
        return useProductionOnly;
    }

    public boolean monoRepoMode() {
        return monoRepoMode;
    }

    public List<String> getExcludedWorkspaceNamePatterns() {
        return excludedWorkspaceNamePatterns;
    }

    public List<String> getIncludedWorkspaceNamePatterns() {
        return includedWorkspaceNamePatterns;
    }
}
