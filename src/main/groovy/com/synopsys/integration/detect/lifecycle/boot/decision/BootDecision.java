package com.synopsys.integration.detect.lifecycle.boot.decision;

public class BootDecision {
    private final BlackDuckDecision blackDuckDecision;
    private final PolarisDecision polarisDecision;

    public BootDecision(final BlackDuckDecision blackDuckDecision, final PolarisDecision polarisDecision) {
        this.blackDuckDecision = blackDuckDecision;
        this.polarisDecision = polarisDecision;
    }

    public BlackDuckDecision getBlackDuckDecision() {
        return blackDuckDecision;
    }

    public PolarisDecision getPolarisDecision() {
        return polarisDecision;
    }

    public boolean willRunAny() {
        return blackDuckDecision.shouldRun() || polarisDecision.shouldRun();
    }
}
