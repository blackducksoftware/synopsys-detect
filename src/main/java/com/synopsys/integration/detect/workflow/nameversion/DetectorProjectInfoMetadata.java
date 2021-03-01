/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.nameversion;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorProjectInfoMetadata {
    @NotNull
    private final DetectorType detectorType;
    private final int depth;

    public DetectorProjectInfoMetadata(@NotNull final DetectorType detectorType, final int depth) {
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
