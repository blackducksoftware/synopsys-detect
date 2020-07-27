package com.synopsys.integration.detect.tool.impactanalysis;

public class ImpactAnalysisOptions {
    private final Boolean enabled;

    public ImpactAnalysisOptions(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isEnabled() {
        return enabled;
    }
}
