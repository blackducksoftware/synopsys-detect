package com.synopsys.integration.detectable.detectables.go;

public class GoModDetectableOptions {
    private final boolean dependencyVerificationEnabled;

    public GoModDetectableOptions(boolean dependencyVerificationEnabled) {
        this.dependencyVerificationEnabled = dependencyVerificationEnabled;
    }

    public boolean isDependencyVerificationEnabled() {
        return dependencyVerificationEnabled;
    }
}
