package com.synopsys.integration.detectable.detectables.conda;

public class CondaCliDetectableOptions {
    private final String condaEnvironmentName;

    public CondaCliDetectableOptions(final String condaEnvironmentName) {
        this.condaEnvironmentName = condaEnvironmentName;
    }

    public String getCondaEnvironmentName() {
        return condaEnvironmentName;
    }
}
