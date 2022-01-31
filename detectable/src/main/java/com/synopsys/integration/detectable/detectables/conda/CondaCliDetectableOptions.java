package com.synopsys.integration.detectable.detectables.conda;

import java.util.Optional;

public class CondaCliDetectableOptions {
    private final String condaEnvironmentName;

    public CondaCliDetectableOptions(String condaEnvironmentName) {
        this.condaEnvironmentName = condaEnvironmentName;
    }

    public Optional<String> getCondaEnvironmentName() {
        return Optional.ofNullable(condaEnvironmentName);
    }
}
