package com.synopsys.integration.detect.battery.docker.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.detect.battery.util.DetectorBatteryTest;
import com.synopsys.integration.detect.commontest.FileUtil;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.util.OperatingSystemType;

@Tag("docker")
public class YarnDockerTest {
    @Test
    void YarnBerryTest() {

    }

    @Test
    void run() throws IOException, InterruptedException {
        String imageName = "docker-battery-yarn";
        String imageTag = imageName + ":latest";
        String containerName = imageName + "-container";

        File imageDockerFile = FileUtil.asFile(DetectorBatteryTest.class, "/Yarn_Berry.dockerfile", "/docker");
        Assertions.assertNotNull(imageDockerFile);

        File root = new File(".");
        File build = new File(root, "build");
        File libs = new File(build, "libs");
        File[] libChildren = libs.listFiles();
        Assertions.assertNotNull(libChildren, "Libs must contains a detect jar!");
        Assertions.assertEquals(1, libChildren.length, "Libs must contains a detect jar!");
        File detectJarFile = libChildren[0];
        Assertions.assertNotNull(detectJarFile, "Libs must contains a detect jar!");
        Assertions.assertTrue(detectJarFile.getName().endsWith(".jar"), "Detect must have the .jar extension!");
        File dockerFile = new File(build, "docker");
        File containerOutput = new File(dockerFile, imageName);

        if (!containerOutput.exists()) {
            Assertions.assertTrue(containerOutput.mkdirs(), String.format("Failed to create container directory at: %s", containerOutput.getAbsolutePath()));
        }
        Assertions.assertTrue(containerOutput.exists(), "The detect container path must exist.");

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

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        List<Image> images = dockerClient.listImagesCmd().exec();

        List<String> tags = images.stream().flatMap(image -> Arrays.stream(image.getRepoTags())).collect(Collectors.toList());
        boolean foundImage = tags.contains(imageTag);
        if (!foundImage) {
            dockerClient.buildImageCmd(imageDockerFile)
                .withTags(Bds.of(imageTag).toSet())
                .exec(new BuildImageResultCallback())
                .awaitImageId();
        }

        String cmd = "java -jar /opt/detect/" + detectJarFile.getName() + arguments("/opt/results/output", "/opt/results/bdio", "/opt/project/src");
        String containerId = dockerClient.createContainerCmd(imageTag)
                                 .withHostConfig(HostConfig.newHostConfig().withBinds(Bind.parse(detectJarFile.getParentFile().getCanonicalPath() + ":/opt/detect"), Bind.parse(containerOutput.getCanonicalPath() + ":/opt/results")))
                                 .withCmd(cmd.split(" "))
                                 .exec().getId();

        dockerClient.startContainerCmd(containerId).exec();

        int exitCode = dockerClient.waitContainerCmd(containerId)
                           .exec(new WaitContainerResultCallback())
                           .awaitStatusCode();

        String logs = dockerClient.logContainerCmd(containerId)
                          .withStdErr(true)
                          .withStdOut(true)
                          .exec(new LogContainerTestCallback()).awaitCompletion().toString();

        Assertions.assertEquals(0, exitCode);
        Assertions.assertFalse(StringUtils.isEmpty(logs));
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();

        File bdioFiles = new File(containerOutput, "bdio");
        Assertions.assertTrue(bdioFiles.length() > 0);
    }

    private String arguments(String outputDirectory, String bdioDirectory, String sourceDirectory) {
        Map<Property, String> properties = new HashMap<>();
        properties.put(DetectProperties.DETECT_TOOLS.getProperty(), "DETECTOR");
        properties.put(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty(), "true");
        properties.put(DetectProperties.DETECT_OUTPUT_PATH.getProperty(), outputDirectory);
        properties.put(DetectProperties.DETECT_BDIO_OUTPUT_PATH.getProperty(), bdioDirectory);
        properties.put(DetectProperties.DETECT_CLEANUP.getProperty(), "false");
        properties.put(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION.getProperty(), "INFO"); // Leave at INFO for Travis. Long logs cause build to fail.
        properties.put(DetectProperties.DETECT_SOURCE_PATH.getProperty(), sourceDirectory);

        StringBuilder builder = new StringBuilder();
        properties.forEach((key, value) -> {
            builder.append(" --").append(key.getKey()).append("=").append(value);
        });
        return builder.toString();
    }

    private File batteryDirectory() {//TODO: Factor commonality to regular battery tests
        File batteryDirectory = new File(System.getenv("BATTERY_TESTS_PATH"));
        if (!batteryDirectory.exists()) {
            Assertions.assertTrue(batteryDirectory.mkdirs(), String.format("Failed to create battery directory at: %s", batteryDirectory.getAbsolutePath()));
        }
        Assertions.assertTrue(batteryDirectory.exists(), "The detect battery path must exist.");
        return batteryDirectory;
    }

    public static class LogContainerTestCallback extends LogContainerResultCallback {
        protected final StringBuilder log = new StringBuilder();

        List collectedFrames = new ArrayList<>();

        boolean collectFrames = false;

        public LogContainerTestCallback() {
            this(false);
        }

        public LogContainerTestCallback(boolean collectFrames) {
            this.collectFrames = collectFrames;
        }

        @Override
        public void onNext(Frame frame) {
            if (collectFrames)
                collectedFrames.add(frame);
            log.append(new String(frame.getPayload()));
        }

        @Override
        public String toString() {
            return log.toString();
        }

        public List getCollectedFrames() {
            return collectedFrames;
        }
    }

}
