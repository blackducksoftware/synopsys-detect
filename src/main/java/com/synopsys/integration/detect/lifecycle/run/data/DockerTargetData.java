package com.synopsys.integration.detect.lifecycle.run.data;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.util.NameVersion;

public class DockerTargetData {

    private NameVersion nameVersion;
    @Nullable
    File squashedDockerTar;
    @Nullable
    File unsquashedDockerTar;

    public static DockerTargetData NO_DOCKER_TARGET = new DockerTargetData(null, null, null);

    public DockerTargetData(final NameVersion nameVersion, @Nullable final File squashedDockerTar, @Nullable final File unsquashedDockerTar) {
        this.nameVersion = nameVersion;
        this.squashedDockerTar = squashedDockerTar;
        this.unsquashedDockerTar = unsquashedDockerTar;
    }

    public NameVersion getNameVersion() {
        return nameVersion;
    }

    public Optional<File> getSquashedDockerTar() {
        return Optional.ofNullable(squashedDockerTar);
    }

    public Optional<File> getUnsquashedDockerTar() {
        return Optional.ofNullable(unsquashedDockerTar);
    }

    public boolean hasTarget() {
        return squashedDockerTar != null || unsquashedDockerTar != null;
    }
}
