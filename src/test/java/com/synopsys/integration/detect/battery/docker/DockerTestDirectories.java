package com.synopsys.integration.detect.battery.docker;

import java.io.File;

public class DockerTestDirectories {
    private final File imageDockerFile;
    private final File dockerTestDirectory;
    private final File containerOutput;
    private final File detectJar;

    public DockerTestDirectories(final File imageDockerFile, final File dockerTestDirectory, final File containerOutput, final File detectJar) {

        this.imageDockerFile = imageDockerFile;
        this.dockerTestDirectory = dockerTestDirectory;
        this.containerOutput = containerOutput;
        this.detectJar = detectJar;
    }

    public File getImageDockerFile() {
        return imageDockerFile;
    }

    public File getDockerTestDirectory() {
        return dockerTestDirectory;
    }

    public File getContainerOutput() {
        return containerOutput;
    }

    public File getDetectJar() {
        return detectJar;
    }
}
