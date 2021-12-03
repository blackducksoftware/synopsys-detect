package com.synopsys.integration.detectable.detectables.dart.pubdep;

public class DartPubDepsDetectableOptions {
    private boolean excludeDevDependencies;

    public DartPubDepsDetectableOptions(boolean excludeDevDependencies) {
        this.excludeDevDependencies = excludeDevDependencies;
    }

    public boolean isExcludeDevDependencies() {
        return excludeDevDependencies;
    }
}
