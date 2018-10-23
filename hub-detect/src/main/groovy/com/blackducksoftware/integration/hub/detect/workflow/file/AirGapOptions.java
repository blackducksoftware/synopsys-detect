package com.blackducksoftware.integration.hub.detect.workflow.file;

public class AirGapOptions {
    private String dockerInspectorPathOverride;
    private String gradleInspectorPathOverride;
    private String nugetInspectorPathOverride;

    public AirGapOptions(final String dockerInspectorPathOverride, final String gradleInspectorPathOverride, final String nugetInspectorPathOverride) {
        this.dockerInspectorPathOverride = dockerInspectorPathOverride;
        this.gradleInspectorPathOverride = gradleInspectorPathOverride;
        this.nugetInspectorPathOverride = nugetInspectorPathOverride;
    }

    public String getDockerInspectorPathOverride() {
        return dockerInspectorPathOverride;
    }

    public String getGradleInspectorPathOverride() {
        return gradleInspectorPathOverride;
    }

    public String getNugetInspectorPathOverride() {
        return nugetInspectorPathOverride;
    }
}
