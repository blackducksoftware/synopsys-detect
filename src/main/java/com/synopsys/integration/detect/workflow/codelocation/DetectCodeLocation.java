/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.codelocation;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class DetectCodeLocation {
    private final DependencyGraph dependencyGraph;
    private final File sourcePath;
    private final ExternalId externalId;
    private final String creatorName;
    private final String dockerImageName;

    private DetectCodeLocation(final DependencyGraph dependencyGraph, final File sourcePath, final ExternalId externalId, final String creatorName,
        final String dockerImageName) {
        this.dependencyGraph = dependencyGraph;
        this.sourcePath = sourcePath;
        this.externalId = externalId;
        this.creatorName = creatorName;
        this.dockerImageName = dockerImageName;

        if (StringUtils.isNotBlank(dockerImageName) && StringUtils.isNotBlank(creatorName)) {
            throw new IllegalArgumentException("Detect code location cannot have the Docker image name and the creator name set as the docker image name will means no creator exists.");
        }
    }

    public static DetectCodeLocation forDocker(final DependencyGraph dependencyGraph, final File sourcePath, final ExternalId externalId, final String dockerImageName) {
        return new DetectCodeLocation(dependencyGraph, sourcePath, externalId, null, dockerImageName);
    }

    public static DetectCodeLocation forCreator(final DependencyGraph dependencyGraph, final File sourcePath, final ExternalId externalId, final String creatorName) {
        return new DetectCodeLocation(dependencyGraph, sourcePath, externalId, creatorName, null);
    }

    public DetectCodeLocation copy(final DependencyGraph dependencyGraph) {
        return new DetectCodeLocation(dependencyGraph, sourcePath, externalId, creatorName, dockerImageName);
    }

    public Optional<String> getCreatorName() {
        return Optional.ofNullable(creatorName);
    }

    public Optional<String> getDockerImageName() {
        return Optional.ofNullable(dockerImageName);
    }

    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public File getSourcePath() {
        return sourcePath;
    }

    public ExternalId getExternalId() {
        return externalId;
    }
}
