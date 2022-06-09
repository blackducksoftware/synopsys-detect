package com.synopsys.integration.detector.result;

import org.jetbrains.annotations.NotNull;

public class FailedDetectorResult extends DetectorResult {
    public FailedDetectorResult(@NotNull String description) {
        super(false, description);
    }
}
