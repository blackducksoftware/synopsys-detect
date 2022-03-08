package com.synopsys.integration.detect.workflow.nameversion;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorProjectInfoMetadata {
    @NotNull
    private final DetectorType detectorType;
    private final int depth;

    public DetectorProjectInfoMetadata(@NotNull DetectorType detectorType, int depth) {
        this.detectorType = detectorType;
        this.depth = depth;
    }

    @NotNull
    public DetectorType getDetectorType() {
        return detectorType;
    }

    public int getDepth() {
        return depth;
    }
}
