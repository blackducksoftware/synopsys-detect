/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio;

public class BdioOptions {
    private final boolean enabledBdio2;
    private final String projectCodeLocationSuffix;
    private final String projectCodeLocationPrefix;

    public BdioOptions(final boolean enabledBdio2, final String projectCodeLocationPrefix, String projectCodeLocationSuffix) {
        this.enabledBdio2 = enabledBdio2;
        this.projectCodeLocationSuffix = projectCodeLocationSuffix;
        this.projectCodeLocationPrefix = projectCodeLocationPrefix;
    }

    public String getProjectCodeLocationSuffix() {
        return projectCodeLocationSuffix;
    }

    public String getProjectCodeLocationPrefix() {
        return projectCodeLocationPrefix;
    }

    public boolean isBdio2Enabled() {
        return enabledBdio2;
    }
}
