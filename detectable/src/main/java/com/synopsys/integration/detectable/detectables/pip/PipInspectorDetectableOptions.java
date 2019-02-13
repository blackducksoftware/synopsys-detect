package com.synopsys.integration.detectable.detectables.pip;

public class PipInspectorDetectableOptions {
    private final String detectPipProjectName;

    public PipInspectorDetectableOptions(final String detectPipProjectName) {
        this.detectPipProjectName = detectPipProjectName;
    }

    public String getDetectPipProjectName() {
        return detectPipProjectName;
    }
}
