package com.synopsys.integration.detect.battery.docker;

import java.io.File;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.battery.util.DetectJar;
import com.synopsys.integration.detect.battery.util.DetectorBatteryTest;
import com.synopsys.integration.detect.battery.util.TestPaths;
import com.synopsys.integration.detect.commontest.FileUtil;

public class DetectDockerTest {
    private final String testId;
    private final String imageName;
    private final String dockerfileResourceName;

    public DetectDockerTest(final String testId, final String imageName, final String dockerfileResourceName) {
        this.testId = testId;
        this.imageName = imageName;
        this.dockerfileResourceName = dockerfileResourceName;
    }

    public DockerTestDirectories setup() {
        File imageDockerFile = FileUtil.asFile(DetectorBatteryTest.class, dockerfileResourceName, "/docker");
        Assertions.assertNotNull(imageDockerFile, "Could not find the dockerfile in the resources, ensure the dockerfile exists as named. It is needed to build the image if the image is not present.");

        File dockerTestDirectory = new File(TestPaths.build(), "docker");
        File containerOutput = new File(dockerTestDirectory, testId);

        if (!containerOutput.exists()) {
            Assertions.assertTrue(containerOutput.mkdirs(), String.format("Failed to create container directory at: %s", containerOutput.getAbsolutePath()));
        }

        Assertions.assertTrue(containerOutput.exists(), "The detect container path must exist.");

        File detectJar = DetectJar.findJar();
        Assertions.assertNotNull(detectJar, "Docker tests require a detect jar.");

        return new DockerTestDirectories(imageDockerFile, dockerTestDirectory, containerOutput, detectJar);
    }

    public DockerDetectResult run(String cmd) {

        DetectDockerRunner detectDockerRunner = new DetectDockerRunner();
        try {
            return detectDockerRunner.runDetect(imageName, imageDockerFile, cmd, detectJar, containerOutput);
        } catch (Exception e) {
            Assertions.assertNull(e, "An exception occurred running a docker test!");
        }

        return null;
    }
}
