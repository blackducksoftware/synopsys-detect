package com.synopsys.integration.detect.battery.docker.provider;

import com.github.dockerjava.api.DockerClient;

public interface DockerImageProvider {
    void installImage(String imageName, DockerClient dockerClient) throws InterruptedException;
}
