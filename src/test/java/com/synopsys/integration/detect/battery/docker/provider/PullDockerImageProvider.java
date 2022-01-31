package com.synopsys.integration.detect.battery.docker.provider;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;

public class PullDockerImageProvider implements DockerImageProvider {
    @Override
    public void installImage(String imageName, DockerClient dockerClient) throws InterruptedException {
        PullImageResultCallback callback = new PullImageResultCallback();
        dockerClient.pullImageCmd(imageName).exec(callback); //I am not sure this is right, it says 'repository' in the args.
        callback.awaitCompletion();
    }
}
