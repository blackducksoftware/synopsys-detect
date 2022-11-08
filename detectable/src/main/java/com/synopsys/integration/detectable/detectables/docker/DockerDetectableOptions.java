package com.synopsys.integration.detectable.detectables.docker;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.log.LogLevel;

public class DockerDetectableOptions {

    private final String suppliedDockerImage;
    private final String suppliedDockerImageId;
    private final String suppliedDockerTar;
    private final LogLevel dockerInspectorLoggingLevel;
    private final String dockerInspectorVersion;
    private final Map<String, String> additionalDockerProperties;
    private final Path dockerInspectorPath;
    private final String dockerPlatformTopLayerId;

    public DockerDetectableOptions(
        String suppliedDockerImage,
        String suppliedDockerImageId,
        String suppliedDockerTar,
        LogLevel dockerInspectorLoggingLevel,
        String dockerInspectorVersion,
        Map<String, String> additionalDockerProperties,
        Path dockerInspectorPath,
        String dockerPlatformTopLayerId
    ) {
        this.suppliedDockerImage = suppliedDockerImage;
        this.suppliedDockerImageId = suppliedDockerImageId;
        this.suppliedDockerTar = suppliedDockerTar;
        this.dockerInspectorLoggingLevel = dockerInspectorLoggingLevel;
        this.dockerInspectorVersion = dockerInspectorVersion;
        this.additionalDockerProperties = additionalDockerProperties;
        this.dockerInspectorPath = dockerInspectorPath;
        this.dockerPlatformTopLayerId = dockerPlatformTopLayerId;
    }

    public Optional<String> getSuppliedDockerImage() {
        return Optional.ofNullable(suppliedDockerImage);
    }

    public Optional<String> getSuppliedDockerImageId() {
        return Optional.ofNullable(suppliedDockerImageId);
    }

    public Optional<String> getSuppliedDockerTar() {
        return Optional.ofNullable(suppliedDockerTar);
    }

    public boolean hasDockerImageOrTar() {
        return getSuppliedDockerImage().isPresent() || getSuppliedDockerTar().isPresent() || getSuppliedDockerImageId().isPresent();
    }

    public LogLevel getDockerInspectorLoggingLevel() {
        return dockerInspectorLoggingLevel;
    }

    public Optional<String> getDockerInspectorVersion() {
        return Optional.ofNullable(dockerInspectorVersion);
    }

    public Map<String, String> getAdditionalDockerProperties() {
        return additionalDockerProperties;
    }

    public Optional<Path> getDockerInspectorPath() {
        return Optional.ofNullable(dockerInspectorPath);
    }

    public Optional<String> getDockerPlatformTopLayerId() {
        return Optional.ofNullable(dockerPlatformTopLayerId);
    }
}
