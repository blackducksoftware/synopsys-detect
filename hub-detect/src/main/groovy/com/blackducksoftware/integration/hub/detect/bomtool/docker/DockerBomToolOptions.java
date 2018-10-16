package com.blackducksoftware.integration.hub.detect.bomtool.docker;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;

public class DockerBomToolOptions {

    private final boolean dockerPathRequired;
    private final String suppliedDockerImage;
    private final String suppliedDockerTar;

    public static DockerBomToolOptions fromConfiguration(DetectConfiguration detectConfiguration) {
        final String tar = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_TAR);
        final String image = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_IMAGE);
        final boolean dockerRequired = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DOCKER_PATH_REQUIRED);
        return new DockerBomToolOptions(dockerRequired, image, tar);
    }

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
