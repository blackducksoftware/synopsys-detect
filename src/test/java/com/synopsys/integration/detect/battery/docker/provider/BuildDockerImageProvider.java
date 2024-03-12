package com.synopsys.integration.detect.battery.docker.provider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.commontest.FileUtil;

public class BuildDockerImageProvider implements DockerImageProvider {
    private final String dockerfileResourceName;
    private Map<String, String> buildArgs = new HashMap<>();

    public BuildDockerImageProvider(String dockerfileResourceName) {
        this.dockerfileResourceName = dockerfileResourceName;
    }

    public static BuildDockerImageProvider forDockerfilResourceNamed(String dockerfileResourceName) {
        return new BuildDockerImageProvider(dockerfileResourceName);
    }

    public void setBuildArgs(Map<String, String> buildArgs) {
        this.buildArgs = buildArgs;
    }

    @Override
    public void installImage(String imageName, DockerClient dockerClient) {
        File imageDockerFile = FileUtil.asFile(DetectorBatteryTestRunner.class, dockerfileResourceName, "/docker/");
        Assertions.assertNotNull(
            imageDockerFile,
            "Could not find the dockerfile in the resources, ensure the dockerfile exists as named. It is needed to build the image if the image is not present."
        );

        try (BuildImageCmd buildImageCmd = dockerClient.buildImageCmd(imageDockerFile)) {

            // If the parent test has provided Dockerfile args, pass them to the image build command
            for (Map.Entry<String, String> entry : buildArgs.entrySet()) {
                buildImageCmd.withBuildArg(entry.getKey(), entry.getValue());
            }

            buildImageCmd
                .withTags(Bds.of(imageName).toSet())
                .exec(new BuildImageResultCallback())
                .awaitImageId();
        }
    }
}
