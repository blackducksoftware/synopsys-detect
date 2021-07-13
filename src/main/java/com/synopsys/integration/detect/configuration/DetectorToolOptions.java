/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration;

import java.util.List;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorToolOptions {
    private final String projectBomTool;
    private final List<DetectorType> requiredDetectors;
    private final boolean buildless;

    public DetectorToolOptions(String projectBomTool, List<DetectorType> requiredDetectors, boolean buildless) {
        this.projectBomTool = projectBomTool;
        this.requiredDetectors = requiredDetectors;
        this.buildless = buildless;
    }

    public String getProjectBomTool() {
        return projectBomTool;
    }

    public List<DetectorType> getRequiredDetectors() {
        return requiredDetectors;
    }

    public boolean isBuildless() {
        return buildless;
    }
}
