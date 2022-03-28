package com.synopsys.integration.detectable.detectables.pipenv.build;

import java.util.Optional;

public class PipenvDetectableOptions {
    private final String pipProjectName;
    private final String pipProjectVersionName;
    private final boolean pipProjectTreeOnly;

    public PipenvDetectableOptions(String pipProjectName, String pipProjectVersionName, boolean pipProjectTreeOnly) {
        this.pipProjectName = pipProjectName;
        this.pipProjectVersionName = pipProjectVersionName;
        this.pipProjectTreeOnly = pipProjectTreeOnly;
    }

    public Optional<String> getPipProjectName() {
        return Optional.ofNullable(pipProjectName);
    }

    public Optional<String> getPipProjectVersionName() {
        return Optional.ofNullable(pipProjectVersionName);
    }

    public boolean isPipProjectTreeOnly() {
        return pipProjectTreeOnly;
    }
}
