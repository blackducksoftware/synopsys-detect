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
