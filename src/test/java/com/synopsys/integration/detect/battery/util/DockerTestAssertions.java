package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.battery.docker.DockerDetectResult;

public class DockerTestAssertions {
    private DockerDetectResult dockerDetectResult;
    private File outputDirectory;
    private File bdioDirectory;

    public DockerTestAssertions(final DockerDetectResult dockerDetectResult) {
        this.dockerDetectResult = dockerDetectResult;
    }

    public void successfulDetectorType(String detectorType) {
        Assertions.assertTrue(dockerDetectResult.getDetectLogs().contains(detectorType + " : SUCCESS"));
    }

    public void atLeastOneBdioFile() {
        Assertions.assertNotNull(bdioDirectory, "Expected at least one bdio file!");
        Assertions.assertNotNull(bdioDirectory.listFiles(), "Expected at least one bdio file!");
        Assertions.assertTrue(Objects.requireNonNull(bdioDirectory.listFiles()).length > 0, "Expected at least one bdio file!");
    }
}
