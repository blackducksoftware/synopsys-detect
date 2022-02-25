package com.synopsys.integration.detectable.detectables.go.gomod;

public class GoModCliDetectableOptions {
    private final GoModDependencyType excludedDependencyTypes;

    public GoModCliDetectableOptions(GoModDependencyType excludedDependencyTypes) {
        this.excludedDependencyTypes = excludedDependencyTypes;
    }

    public GoModDependencyType getExcludedDependencyTypes() {
        return excludedDependencyTypes;
    }
}
