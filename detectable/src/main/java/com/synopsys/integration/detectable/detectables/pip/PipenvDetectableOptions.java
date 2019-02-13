package com.synopsys.integration.detectable.detectables.pip;

public class PipenvDetectableOptions {
    private final String pipProjectName;
    private final String pipProjectVersionName;

    public PipenvDetectableOptions(final String pipProjectName, final String pipProjectVersionName) {
        this.pipProjectName = pipProjectName;
        this.pipProjectVersionName = pipProjectVersionName;
    }

    public String getPipProjectName() {
        return pipProjectName;
    }

    public String getPipProjectVersionName() {
        return pipProjectVersionName;
    }
}
