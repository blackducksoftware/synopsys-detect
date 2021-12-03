package com.synopsys.integration.detector.result;

import java.util.Collections;

import org.jetbrains.annotations.NotNull;

public class FailedDetectorResult extends DetectorResult {
    public FailedDetectorResult(@NotNull String description, Class resultClass) {
        super(false, description, resultClass, Collections.emptyList(), Collections.emptyList());
    }
}
