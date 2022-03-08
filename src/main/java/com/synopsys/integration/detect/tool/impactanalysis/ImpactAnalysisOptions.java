package com.synopsys.integration.detect.tool.impactanalysis;

import org.jetbrains.annotations.Nullable;

public class ImpactAnalysisOptions {
    @Nullable
    private final String codeLocationPrefix;
    @Nullable
    private final String codeLocationSuffix;

    public ImpactAnalysisOptions(@Nullable String codeLocationPrefix, @Nullable String codeLocationSuffix) {
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
    }

    @Nullable
    public String getCodeLocationPrefix() {
        return codeLocationPrefix;
    }

    @Nullable
    public String getCodeLocationSuffix() {
        return codeLocationSuffix;
    }
}
