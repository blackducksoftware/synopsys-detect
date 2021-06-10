package com.synopsys.integration.detect.battery.docker;

import java.io.File;

import org.junit.jupiter.api.Assertions;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.synopsys.integration.detect.battery.docker.provider.DockerImageProvider;
import com.synopsys.integration.detect.battery.util.DetectJar;
import com.synopsys.integration.detect.battery.util.DockerTestAssertions;
import com.synopsys.integration.detect.battery.util.TestPaths;
import com.synopsys.integration.detect.configuration.DetectProperties;

public class DetectDockerTest {
    private final String testId;
    private final String imageName;
    private DockerImageProvider dockerImageProvider;
    private String projectSourceDirectory = "/opt/project/src";
    private static String imageResultsPath = "/opt/results"; // (= test output)
    private static String imageResultsOutputPath = "/opt/results/output";
    private static String imageResultsBdioPath = "/opt/results/bdio";
    private static String imageDetectPath = "/opt/detect";

    public DetectDockerTest(final String testId, final String imageName) {
        this.testId = testId;
        this.imageName = imageName;
    }

    public void withProjectSourceDirectory(String projectSourceDirectory) {
        this.projectSourceDirectory = projectSourceDirectory;
    }

    public void withImageProvider(DockerImageProvider dockerImageProvider) {
        this.dockerImageProvider = dockerImageProvider;
    }

    private DockerTestDirectories setup() {
        File dockerTestDirectory = new File(TestPaths.build(), "docker");
        File resultDirectory = new File(dockerTestDirectory, testId);
        File resultOutputDirectory = new File(resultDirectory, "output");
        File resultBdioDirectory = new File(resultDirectory, "bdio");

        if (!dockerTestDirectory.exists()) {
            Assertions.assertTrue(dockerTestDirectory.mkdirs(), String.format("Failed to create container directory at: %s", dockerTestDirectory.getAbsolutePath()));
        }
        if (!resultDirectory.exists()) {
            Assertions.assertTrue(resultDirectory.mkdirs(), String.format("Failed to create container directory at: %s", resultDirectory.getAbsolutePath()));
        }

        if (resultOutputDirectory.exists()) {
            Assertions.assertTrue(resultOutputDirectory.delete(), String.format("Failed to create container directory at: %s", resultOutputDirectory.getAbsolutePath()));
        }
        Assertions.assertTrue(resultOutputDirectory.mkdir(), String.format("Failed to create container directory at: %s", resultOutputDirectory.getAbsolutePath()));

        if (resultBdioDirectory.exists()) {
            Assertions.assertTrue(resultBdioDirectory.delete(), String.format("Failed to create container directory at: %s", resultBdioDirectory.getAbsolutePath()));
        }
        Assertions.assertTrue(resultBdioDirectory.mkdir(), String.format("Failed to create container directory at: %s", resultOutputDirectory.getAbsolutePath()));

        File detectJar = DetectJar.findJar();
        Assertions.assertNotNull(detectJar, "Docker tests require a detect jar.");

        return new DockerTestDirectories(dockerTestDirectory, resultDirectory, resultOutputDirectory, resultBdioDirectory, detectJar);
    }

    private DockerDetectResult runContainer(DockerTestDirectories dockerTestDirectories, DetectCommandBuilder detectCommandBuilder) {
        DetectDockerRunner detectDockerRunner = new DetectDockerRunner();
        try {
            DockerClient dockerClient = detectDockerRunner.connectToDocker();
            if (!detectDockerRunner.imageExists(imageName, dockerClient)) {
                Assertions.assertNotNull(dockerImageProvider, "Image could not be found and no image provider was configured.");
                dockerImageProvider.installImage(imageName, dockerClient);
                Assertions.assertTrue(detectDockerRunner.imageExists(imageName, dockerClient), "Image provider was unable to install the image. Not sure how to help :(");
            }

            detectCommandBuilder.property(DetectProperties.DETECT_OUTPUT_PATH, imageResultsOutputPath);
            detectCommandBuilder.property(DetectProperties.DETECT_BDIO_OUTPUT_PATH, imageResultsBdioPath);
            detectCommandBuilder.property(DetectProperties.DETECT_SOURCE_PATH, projectSourceDirectory);

            Bind detectBinding = Bind.parse(dockerTestDirectories.getDetectJar().getParentFile().getCanonicalPath() + ":" + imageDetectPath); //mounts access to detect jar, could also be used for 'tools' directory.
            Bind outputBinding = Bind.parse(dockerTestDirectories.getResultDirectory() + ":" + imageResultsPath);
            HostConfig hostConfig = HostConfig.newHostConfig().withBinds(detectBinding, outputBinding);

            String cmd = "java -jar /opt/detect/" + dockerTestDirectories.getDetectJar().getName() + detectCommandBuilder.buildCommand();

            return detectDockerRunner.runContainer(imageName, cmd, hostConfig, dockerClient);
        } catch (Exception e) {
            Assertions.assertNull(e, "An exception occurred running a docker test!");
        }

        return null;
    }

    public DockerTestAssertions run(DetectCommandBuilder commandBuilder) {
        DockerTestDirectories directories = setup();
        DockerDetectResult result = runContainer(directories, commandBuilder);
        return new DockerTestAssertions(result);
    }
}
