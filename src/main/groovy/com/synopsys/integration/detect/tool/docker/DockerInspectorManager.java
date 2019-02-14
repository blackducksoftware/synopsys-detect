/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detect.tool.docker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.file.AirGapManager;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;

public class DockerInspectorManager {
    private static final String IMAGE_INSPECTOR_FAMILY = "blackduck-imageinspector";
    private static final List<String> inspectorNames = Arrays.asList("ubuntu", "alpine", "centos");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String DOCKER_SHARED_DIRECTORY_NAME = "docker";

    private final DirectoryManager directoryManager;
    private final AirGapManager airGapManager;
    private final DetectFileFinder detectFileFinder;
    private final DetectConfiguration detectConfiguration;
    private final ArtifactResolver artifactResolver;

    private DockerInspectorInfo resolvedInfo;
    private boolean hasResolvedInspector;

    public DockerInspectorManager(final DirectoryManager directoryManager, AirGapManager airGapManager, final DetectFileFinder detectFileFinder,
        final DetectConfiguration detectConfiguration, final ArtifactResolver artifactResolver) {
        this.directoryManager = directoryManager;
        this.airGapManager = airGapManager;
        this.detectFileFinder = detectFileFinder;
        this.detectConfiguration = detectConfiguration;
        this.artifactResolver = artifactResolver;
    }

    public DockerInspectorInfo getDockerInspector() throws DetectorException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedInfo = install();
            }
            return resolvedInfo;
        } catch (final Exception e) {
            throw new DetectorException(e);
        }
    }

    private DockerInspectorInfo install() throws IntegrationException, DetectUserFriendlyException, IOException {
        File airGapDockerFolder = new File(airGapManager.getDockerInspectorAirGapPath());
        final String providedJarPath = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_PATH, PropertyAuthority.None);

        if (StringUtils.isNotBlank(providedJarPath)) {
            logger.info("Docker tool will attempt to use the provided docker inspector.");
            return findProvidedJar(providedJarPath);
        } else if (airGapDockerFolder.exists()) {
            logger.info("Docker tool will attempt to use the air gapped docker inspector.");
            return findAirGapInspector();
        } else {
            logger.info("Docker tool will attempt to download or find docker inspector.");
            return findOrDownloadJar();
        }
    }

    private DockerInspectorInfo findAirGapInspector() {
        DockerInspectorInfo info = null;
        final File dockerInspectorJar = getAirGapJar();
        if (dockerInspectorJar != null) {
            final List<File> airGapInspectorImageTarfiles = getAirGapInspectorImageTarfiles();
            info = new DockerInspectorInfo(dockerInspectorJar, airGapInspectorImageTarfiles);
        }
        return info;
    }

    private List<File> getAirGapInspectorImageTarfiles() {
        List<File> airGapInspectorImageTarfiles;
        airGapInspectorImageTarfiles = new ArrayList<>();
        final String dockerInspectorAirGapPath = airGapManager.getDockerInspectorAirGapPath();
        for (final String inspectorName : inspectorNames) {
            final File osImage = new File(dockerInspectorAirGapPath, IMAGE_INSPECTOR_FAMILY + "-" + inspectorName + ".tar");
            airGapInspectorImageTarfiles.add(osImage);
        }
        return airGapInspectorImageTarfiles;
    }

    private DockerInspectorInfo findProvidedJar(String providedJarPath) {
        logger.debug("Checking for user-specified disk-resident docker inspector jar file");
        File providedJar = null;
        if (StringUtils.isNotBlank(providedJarPath)) {
            logger.debug(String.format("Using user-provided docker inspector jar path: %s", providedJarPath));
            final File providedJarCandidate = new File(providedJarPath);
            if (providedJarCandidate.isFile()) {
                logger.debug(String.format("Found user-specified jar: %s", providedJarCandidate.getAbsolutePath()));
                providedJar = providedJarCandidate;
            }
        }
        return new DockerInspectorInfo(providedJar);
    }

    private File getAirGapJar() {
        final String airGapDirPath = airGapManager.getDockerInspectorAirGapPath();
        logger.debug(String.format("Checking for air gap docker inspector jar file in: %s", airGapDirPath));
        try {
            final List<File> possibleJars = detectFileFinder.findFilesToDepth(airGapDirPath, "*.jar", 1);
            if (possibleJars == null || possibleJars.size() == 0) {
                logger.error("Unable to locate air gap jar.");
                return null;
            } else {
                File airGapJarFile = possibleJars.get(0);
                logger.info(String.format("Found air gap docker inspector: %s", airGapJarFile.getAbsolutePath()));
                return airGapJarFile;
            }
        } catch (final Exception e) {
            logger.debug(String.format("Did not find a docker inspector jar file in the airgap dir: %s", airGapDirPath));
            return null;
        }
    }

    private DockerInspectorInfo findOrDownloadJar() throws IntegrationException, DetectUserFriendlyException, IOException {
        logger.info("Determining the location of the Docker inspector.");
        String dockerVersion = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_VERSION, PropertyAuthority.None);
        Optional<String> location = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.DOCKER_INSPECTOR_REPO, ArtifactoryConstants.DOCKER_INSPECTOR_PROPERTY, dockerVersion,
            ArtifactoryConstants.DOCKER_INSPECTOR_VERSION_OVERRIDE);
        if (location.isPresent()) {
            logger.info("Finding or downloading the docker inspector.");
            File dockerDirectory = directoryManager.getPermanentDirectory(DOCKER_SHARED_DIRECTORY_NAME);
            logger.debug(String.format("Downloading docker inspector from '%s' to '%s'.", location.get(), dockerDirectory.getAbsolutePath()));
            File jarFile = artifactResolver.downloadOrFindArtifact(dockerDirectory, location.get());
            logger.info("Found online docker inspector: " + jarFile.getAbsolutePath());
            return new DockerInspectorInfo(jarFile);
        } else {
            throw new DetectorException("Unable to find Docker version from artifactory.");
        }
    }
}
