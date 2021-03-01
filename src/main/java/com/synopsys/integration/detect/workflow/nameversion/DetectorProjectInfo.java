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
import com.synopsys.integration.util.NameVersion;

public class DetectorProjectInfo {
    @NotNull
    private final DetectorType detectorType;
    private final int depth;
    @NotNull
    private final NameVersion nameVersion;

    public DetectorProjectInfo(@NotNull final DetectorType detectorType, final int depth, @NotNull final NameVersion nameVersion) {
        this.detectorType = detectorType;
        this.depth = depth;
        this.nameVersion = nameVersion;
    }

    @NotNull
    public DetectorType getDetectorType() {
        return detectorType;
    }

    public int getDepth() {
        return depth;
    }

    @NotNull
    public NameVersion getNameVersion() {
        return nameVersion;
    }
}
