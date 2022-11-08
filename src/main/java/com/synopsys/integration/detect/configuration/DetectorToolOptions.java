package com.synopsys.integration.detect.configuration;

import java.util.List;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorToolOptions {
    private final String projectBomTool;
    private final List<DetectorType> requiredDetectors;
    private final ExcludeIncludeEnumFilter<DetectorType> requiredAccuracyTypes;

    public DetectorToolOptions(
        String projectBomTool,
        List<DetectorType> requiredDetectors,
        ExcludeIncludeEnumFilter<DetectorType> requiredAccuracyTypes
    ) {
        this.projectBomTool = projectBomTool;
        this.requiredDetectors = requiredDetectors;
        this.requiredAccuracyTypes = requiredAccuracyTypes;
    }

    public String getProjectBomTool() {
        return projectBomTool;
    }

    public List<DetectorType> getRequiredDetectors() {
        return requiredDetectors;
    }

    public ExcludeIncludeEnumFilter<DetectorType> getRequiredAccuracyTypes() {
        return requiredAccuracyTypes;
    }
}
