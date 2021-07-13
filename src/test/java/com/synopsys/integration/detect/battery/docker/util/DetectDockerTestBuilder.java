package com.synopsys.integration.detect.battery.docker.util;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.HostConfig;
import com.synopsys.integration.detect.battery.docker.provider.DockerImageProvider;
import com.synopsys.integration.detect.battery.util.DetectJar;

public class DetectDockerTestBuilder {
    private final String testId;
    private final String imageName;
    private DockerImageProvider dockerImageProvider;
    private DockerTestDirectories dockerTestDirectories;
    private String workingDirectory = "/opt/project/src";

    public DetectDockerTestBuilder(final String testId, final String imageName) throws IOException {
        this.testId = testId;
        this.imageName = imageName;
        this.dockerTestDirectories = new DockerTestDirectories(testId);
    }

    public void withImageProvider(DockerImageProvider dockerImageProvider) {
        this.dockerImageProvider = dockerImageProvider;
    }

    private DockerDetectResult runContainer(DetectCommandBuilder detectCommandBuilder) {
        DetectDockerRunner detectDockerRunner = new DetectDockerRunner();
        DockerClient dockerClient = null;
        try {
            dockerClient = detectDockerRunner.connectToDocker();
        } catch (Exception e) {
            Assertions.fail("Unable to connect to Docker. Integration tests now require docker. Please ensure a docker daemon is running and connectable.", e);
        }
        Assertions.assertNotNull(dockerClient, "Unable to connect to Docker. Integration tests now require docker. Please ensure a docker daemon is running and connectable.");
        try {
            if (!detectDockerRunner.imageExists(imageName, dockerClient)) {
                Assertions.assertNotNull(dockerImageProvider, "Image could not be found and no image provider was configured.");
                dockerImageProvider.installImage(imageName, dockerClient);
                Assertions.assertTrue(detectDockerRunner.imageExists(imageName, dockerClient), "Image provider was unable to install the image. Not sure how to help :(");
            }

            File detectJar = DetectJar.findJar();
            String cmd = "java -jar /opt/detect/" + detectJar.getName() + detectCommandBuilder.buildCommand();
            this.dockerTestDirectories.withBinding(detectJar.getParentFile(), "/opt/detect/");

            HostConfig hostConfig = new HostConfig().withBinds(this.dockerTestDirectories.getBindings());
            return detectDockerRunner.runContainer(imageName, cmd, workingDirectory, hostConfig, dockerClient);
        } catch (Exception e) {
            Assertions.assertNull(e, "An exception occurred running a docker test! ");
        }

        return null;
    }

    public DockerAssertions run(DetectCommandBuilder commandBuilder) {
        DockerDetectResult result = runContainer(commandBuilder);
        return new DockerAssertions(this.dockerTestDirectories, result);
    }

    public DockerTestDirectories directories() {
        return dockerTestDirectories;
    }
}
