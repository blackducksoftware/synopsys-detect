package com.synopsys.integration.detect.battery.docker.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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
    private static String imageToolPath = "/opt/tools";

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
        File toolsDirectory = new File(dockerTestDirectory, "tools");
        File resultDirectory = new File(dockerTestDirectory, testId);
        File resultOutputDirectory = new File(resultDirectory, "output");
        File resultBdioDirectory = new File(resultDirectory, "bdio");

        if (!dockerTestDirectory.exists()) {
            Assertions.assertTrue(dockerTestDirectory.mkdirs(), String.format("Failed to create container directory at: %s", dockerTestDirectory.getAbsolutePath()));
        }
        if (!toolsDirectory.exists()) {
            Assertions.assertTrue(toolsDirectory.mkdirs(), String.format("Failed to create container directory at: %s", dockerTestDirectory.getAbsolutePath()));
        }
        if (!resultDirectory.exists()) {
            Assertions.assertTrue(resultDirectory.mkdirs(), String.format("Failed to create container directory at: %s", resultDirectory.getAbsolutePath()));
        }

        if (resultOutputDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(resultOutputDirectory);
            } catch (IOException e) {
                Assertions.assertNull(e, "Could not delete results directory.");
            }
        }
        Assertions.assertTrue(resultOutputDirectory.mkdir(), String.format("Failed to create container directory at: %s", resultOutputDirectory.getAbsolutePath()));

        if (resultBdioDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(resultBdioDirectory);
            } catch (IOException e) {
                Assertions.assertNull(e, "Could not delete bdio directory.");
            }
        }
        Assertions.assertTrue(resultBdioDirectory.mkdir(), String.format("Failed to create container directory at: %s", resultOutputDirectory.getAbsolutePath()));

        File detectJar = DetectJar.findJar();
        Assertions.assertNotNull(detectJar, "Docker tests require a detect jar.");

        return new DockerTestDirectories(dockerTestDirectory, toolsDirectory, resultDirectory, resultOutputDirectory, resultBdioDirectory, detectJar);
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
            detectCommandBuilder.property(DetectProperties.DETECT_TOOLS_OUTPUT_PATH, imageToolPath);

            Bind detectBinding = Bind.parse(dockerTestDirectories.getDetectJar().getParentFile().getCanonicalPath() + ":" + imageDetectPath); //mounts access to detect jar, could also be used for 'tools' directory.
            Bind outputBinding = Bind.parse(dockerTestDirectories.getResultDirectory().getCanonicalPath() + ":" + imageResultsPath);
            Bind toolBinding = Bind.parse(dockerTestDirectories.getToolsDirectory().getCanonicalPath() + ":" + imageToolPath);
            HostConfig hostConfig = HostConfig.newHostConfig().withBinds(detectBinding, outputBinding, toolBinding);

            String cmd = "java -jar /opt/detect/" + dockerTestDirectories.getDetectJar().getName() + detectCommandBuilder.buildCommand();

            return detectDockerRunner.runContainer(imageName, cmd, hostConfig, dockerClient);
        } catch (Exception e) {
            Assertions.assertNull(e, "An exception occurred running a docker test! ");
        }

        return null;
    }

    public DockerTestAssertions run(DetectCommandBuilder commandBuilder) {
        DockerTestDirectories directories = setup();
        DockerDetectResult result = runContainer(directories, commandBuilder);
        return new DockerTestAssertions(result);
    }
}
