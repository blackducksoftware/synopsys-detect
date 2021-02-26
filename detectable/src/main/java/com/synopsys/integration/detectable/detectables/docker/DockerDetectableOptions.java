/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.docker;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.log.LogLevel;

public class DockerDetectableOptions {

    private final boolean dockerPathRequired;
    private final String suppliedDockerImage;
    private final String suppliedDockerImageId;
    private final String suppliedDockerTar;
    private final LogLevel dockerInspectorLoggingLevel;
    private final String dockerInspectorVersion;
    private final Map<String, String> additionalDockerProperties;
    private final Path dockerInspectorPath;
    private final String dockerPlatformTopLayerId;

    public DockerDetectableOptions(final boolean dockerPathRequired, final String suppliedDockerImage, final String suppliedDockerImageId, final String suppliedDockerTar, final LogLevel dockerInspectorLoggingLevel,
        final String dockerInspectorVersion, final Map<String, String> additionalDockerProperties, final Path dockerInspectorPath, final String dockerPlatformTopLayerId) {
        this.dockerPathRequired = dockerPathRequired;
        this.suppliedDockerImage = suppliedDockerImage;
        this.suppliedDockerImageId = suppliedDockerImageId;
        this.suppliedDockerTar = suppliedDockerTar;
        this.dockerInspectorLoggingLevel = dockerInspectorLoggingLevel;
        this.dockerInspectorVersion = dockerInspectorVersion;
        this.additionalDockerProperties = additionalDockerProperties;
        this.dockerInspectorPath = dockerInspectorPath;
        this.dockerPlatformTopLayerId = dockerPlatformTopLayerId;
    }

    public boolean isDockerPathRequired() {
        return dockerPathRequired;
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
