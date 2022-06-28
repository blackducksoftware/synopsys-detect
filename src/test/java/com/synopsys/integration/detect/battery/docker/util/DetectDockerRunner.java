package com.synopsys.integration.detect.battery.docker.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.synopsys.integration.util.OperatingSystemType;

public class DetectDockerRunner {
    public DockerDetectResult runContainer(String image, String cmd, String workdir, HostConfig hostConfig, DockerClient dockerClient) {
        String detectContainerName = String.format(
            "detect-test%s",
            java.util.UUID.randomUUID()
        ); // this will identify any containers left behind on build server by Detect Docker tests

        CreateContainerCmd command = dockerClient.createContainerCmd(image)
            .withHostConfig(hostConfig)
            .withCmd(cmd.split(" "))
            .withWorkingDir(workdir)
            .withName(detectContainerName);

        String username = System.getProperty("DETECT_DOCKER_USERNAME");
        if (StringUtils.isNotBlank(username)) {
            System.out.println("Using username for docker: " + username);
            command.withUser(username);
        }

        String containerId = command.exec().getId();

        try {
            return runContainer(dockerClient, containerId);
        } catch (Exception e) {
            Assertions.assertNull(e, "An exception occurred running docker commands.");
        } finally {
            dockerClient.removeContainerCmd(containerId).withRemoveVolumes(true).exec();
        }

        Assertions.fail("Should have already returned! Something has gone wrong!");
        return null;
    }

    @NotNull
    private DockerDetectResult runContainer(DockerClient dockerClient, String containerId) throws InterruptedException {
        dockerClient.startContainerCmd(containerId).exec();

        int exitCode = dockerClient.waitContainerCmd(containerId)
            .exec(new WaitContainerResultCallback())
            .awaitStatusCode();

        String logs = dockerClient.logContainerCmd(containerId)
            .withStdErr(true)
            .withStdOut(true)
            .exec(new LogContainerTestCallback()).awaitCompletion().toString();
        
        try {
            dockerClient.stopContainerCmd(containerId).exec();
        } catch (NotModifiedException e) {
            //Container already stopped, we do not care.
        }

        return new DockerDetectResult(exitCode, logs);
    }

    public DockerClient connectToDocker() {
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        // The java-docker library's default docker host value is the Linux/Mac default value, so no action required
        // But for Windows, unless told not to: use the Windows default docker host value
        if (OperatingSystemType.determineFromSystem() == OperatingSystemType.WINDOWS) {
            builder.withDockerHost("npipe:////./pipe/docker_engine");
        }
        DockerClientConfig config = builder.build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }

    public boolean imageExists(String imageName, DockerClient dockerClient) {
        List<Image> images = dockerClient.listImagesCmd().exec();
        List<String> tags = images.stream()
            .filter(image -> image.getRepoTags() != null)
            .flatMap(image -> Arrays.stream(image.getRepoTags()))
            .collect(Collectors.toList());
        return tags.contains(imageName);
    }
}
