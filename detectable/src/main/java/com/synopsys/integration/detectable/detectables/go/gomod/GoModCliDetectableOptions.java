package com.synopsys.integration.detectable.detectables.go.gomod;

public class GoModCliDetectableOptions {
    private final GoModDependencyType goModDependencyType;

    public GoModCliDetectableOptions(GoModDependencyType goModDependencyType) {
        this.goModDependencyType = goModDependencyType;
    }

    public GoModDependencyType getGoModDependencyType() {
        return goModDependencyType;
    }
}
