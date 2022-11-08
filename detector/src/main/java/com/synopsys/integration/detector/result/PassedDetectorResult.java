package com.synopsys.integration.detector.result;

import org.jetbrains.annotations.NotNull;

public class PassedDetectorResult extends DetectorResult {
    public PassedDetectorResult() {
        this("Passed");
    }

    public PassedDetectorResult(@NotNull String description) {
        super(true, description);
    }
}
