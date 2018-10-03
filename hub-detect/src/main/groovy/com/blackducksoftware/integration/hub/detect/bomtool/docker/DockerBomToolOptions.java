package com.blackducksoftware.integration.hub.detect.bomtool.docker;

public class DockerBomToolOptions {

    private final boolean dockerPathRequired;
    private final String suppliedDockerImage;
    private final String suppliedDockerTar;

    public DockerBomToolOptions(final boolean dockerPathRequired, final String suppliedDockerImage,
        final String suppliedDockerTar) {
        this.dockerPathRequired = dockerPathRequired;
        this.suppliedDockerImage = suppliedDockerImage;
        this.suppliedDockerTar = suppliedDockerTar;
    }

    public boolean isDockerPathRequired() {
        return dockerPathRequired;
    }

    public String getSuppliedDockerImage() {
        return suppliedDockerImage;
    }

    public String getSuppliedDockerTar() {
        return suppliedDockerTar;
    }
}
