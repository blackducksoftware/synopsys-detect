/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.parse;

import java.util.List;

public class SbtResolutionCacheOptions {
    private final List<String> includedConfigurations;
    private final List<String> excludedConfigurations;
    private final int reportDepth;

    public SbtResolutionCacheOptions(List<String> includedConfigurations, List<String> excludedConfigurations, int reportDepth) {
        this.includedConfigurations = includedConfigurations;
        this.excludedConfigurations = excludedConfigurations;
        this.reportDepth = reportDepth;
    }

    public List<String> getIncludedConfigurations() {
        return includedConfigurations;
    }

    public List<String> getExcludedConfigurations() {
        return excludedConfigurations;
    }

    public int getReportDepth() {
        return reportDepth;
    }
}
