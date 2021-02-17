/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
