package com.synopsys.integration.detectable.detectables.gradle.model;

public class GradleGav {
    private String artifact;
    private String version;
    private String name;

    public GradleGav(final String artifact, final String version, final String name) {
        this.artifact = artifact;
        this.version = version;
        this.name = name;
    }

    public String getArtifact() {
        return artifact;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }
}
