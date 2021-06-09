package com.synopsys.integration.detectable.detectables.go.gomod;

public class GoModCliDetectableOptions {
    private final boolean dependencyVerificationEnabled;

    public GoModCliDetectableOptions(boolean dependencyVerificationEnabled) {
        this.dependencyVerificationEnabled = dependencyVerificationEnabled;
    }

    public boolean isDependencyVerificationEnabled() {
        return dependencyVerificationEnabled;
    }
}
