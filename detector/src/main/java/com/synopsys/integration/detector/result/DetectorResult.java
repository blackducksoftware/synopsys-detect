package com.synopsys.integration.detector.result;

import org.jetbrains.annotations.NotNull;

public class DetectorResult { //TODO: (detectors) could be renamed SearchResult
    private final boolean passed;
    @NotNull
    private final String description;

    public DetectorResult(boolean passed, @NotNull String description) {
        this.passed = passed;
        this.description = description;
    }

    public boolean getPassed() {
        return passed;
    }

    @NotNull
    public String getDescription() {
        return description;
    }
}
