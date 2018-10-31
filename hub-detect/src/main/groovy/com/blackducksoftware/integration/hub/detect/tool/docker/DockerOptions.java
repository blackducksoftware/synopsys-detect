package com.blackducksoftware.integration.hub.detect.tool.docker;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;

public class DockerOptions {

    private final boolean dockerPathRequired;
    private final String suppliedDockerImage;
    private final String suppliedDockerTar;

    public static DockerOptions fromConfiguration(DetectConfiguration detectConfiguration) {
        final String tar = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_TAR, PropertyAuthority.None);
        final String image = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_IMAGE, PropertyAuthority.None);
        final boolean dockerRequired = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DOCKER_PATH_REQUIRED, PropertyAuthority.None);
        return new DockerOptions(dockerRequired, image, tar);
    }

    public DockerOptions(final boolean dockerPathRequired, final String suppliedDockerImage,
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

    public boolean hasDockerImageOrTag() {
        return StringUtils.isNotBlank(getSuppliedDockerImage()) && StringUtils.isNotBlank(getSuppliedDockerTar());
    }
}
