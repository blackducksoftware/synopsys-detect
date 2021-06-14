package com.synopsys.integration.detect.battery.docker.util;

import java.io.File;

public class DockerTestDirectories {
    private final File dockerDirectory;
    private final File toolsDirectory;
    private final File resultDirectory;
    private final File resultBdioDirectory;
    private final File resultOutputDirectory;
    private final File detectJar;

    public DockerTestDirectories(final File dockerDirectory, final File toolsDirectory, final File resultDirectory, File resultBdioDirectory, File resultOutputDirectory, final File detectJar) {
        this.dockerDirectory = dockerDirectory;
        this.toolsDirectory = toolsDirectory;
        this.resultDirectory = resultDirectory;
        this.resultBdioDirectory = resultBdioDirectory;
        this.resultOutputDirectory = resultOutputDirectory;
        this.detectJar = detectJar;
    }

    public File getDockerDirectory() {
        return dockerDirectory;
    }

    public File getDetectJar() {
        return detectJar;
    }

    public File getResultDirectory() {
        return resultDirectory;
    }

    public File getResultBdioDirectory() {
        return resultBdioDirectory;
    }

    public File getResultOutputDirectory() {
        return resultOutputDirectory;
    }

    public File getToolsDirectory() {
        return toolsDirectory;
    }
}
