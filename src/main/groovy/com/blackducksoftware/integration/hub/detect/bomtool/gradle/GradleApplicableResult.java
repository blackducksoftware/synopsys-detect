package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class GradleApplicableResult extends BomToolApplicableResult {
    private final File buildGradle;
    private final String gradleExe;

    public GradleApplicableResult(final File directory, final File buildGradle, final String gradleExe) {
        super(directory, BomToolType.GRADLE);
        this.buildGradle = buildGradle;
        this.gradleExe = gradleExe;
    }

    public File getBuildGradle() {
        return buildGradle;
    }

    public String getGradleExe() {
        return gradleExe;
    }

}