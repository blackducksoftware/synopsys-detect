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
package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.exception.IntegrationException;

public class DockerInspectorInstaller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ArtifactResolver artifactResolver;

    public DockerInspectorInstaller(final ArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver;
    }

    public File installJar(final File dockerDirectory, final Optional<String> dockerVersion) throws IntegrationException, IOException, DetectUserFriendlyException {
        logger.info("Determining the location of the Docker inspector.");
        final String location = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.DOCKER_INSPECTOR_REPO, ArtifactoryConstants.DOCKER_INSPECTOR_PROPERTY, dockerVersion.orElse(""),
            ArtifactoryConstants.DOCKER_INSPECTOR_VERSION_OVERRIDE);
        return download(location, dockerDirectory);
    }

    public File installAirGap(final File dockerDirectory) throws IntegrationException, IOException, DetectUserFriendlyException {
        logger.info("Determining the location of the Docker inspector.");
        final String location = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.DOCKER_INSPECTOR_REPO, ArtifactoryConstants.DOCKER_INSPECTOR_AIR_GAP_PROPERTY, "", "");
        return download(location, dockerDirectory);
    }

    private File download(final String location, final File dockerDirectory) throws IntegrationException, IOException, DetectUserFriendlyException {
        logger.info("Finding or downloading the docker inspector.");
        logger.debug(String.format("Downloading docker inspector from '%s' to '%s'.", location, dockerDirectory.getAbsolutePath()));
        final File jarFile = artifactResolver.downloadOrFindArtifact(dockerDirectory, location);
        logger.info("Found online docker inspector: " + jarFile.getAbsolutePath());
        return jarFile;
    }

}
