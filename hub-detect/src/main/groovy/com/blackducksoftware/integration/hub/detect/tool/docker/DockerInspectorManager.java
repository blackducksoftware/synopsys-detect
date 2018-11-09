/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.tool.docker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.workflow.ArtifactResolver;
import com.blackducksoftware.integration.hub.detect.workflow.ArtifactoryConstants;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;

public class DockerInspectorManager {
    private static final String IMAGE_INSPECTOR_FAMILY = "blackduck-imageinspector-ws";
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

    private DockerInspectorInfo install() throws DetectorException {
        DockerInspectorInfo dockerInspectorInfo = getInfoBasedOnConfiguredJar();
        if (dockerInspectorInfo != null) {
            return dockerInspectorInfo;
        }
        dockerInspectorInfo = getInfoBasedOnAirGapFiles();
        if (dockerInspectorInfo != null) {
            return dockerInspectorInfo;
        }
        return getInfoBasedOnDownloadedJar();
    }

    private DockerInspectorInfo getInfoBasedOnConfiguredJar() {
        final File dockerInspectorJar = getConfiguredJar();
        return getInfoBasedOnJar(dockerInspectorJar);
    }

    private DockerInspectorInfo getInfoBasedOnAirGapFiles() {
        DockerInspectorInfo info = null;
        final File dockerInspectorJar = getAirGapJar();
        if (dockerInspectorJar != null) {
            final List<File> airGapInspectorImageTarfiles = getAirGapInspectorImageTarfiles();
            info = new DockerInspectorInfo(dockerInspectorJar, airGapInspectorImageTarfiles);
        }
        return info;
    }

    private DockerInspectorInfo getInfoBasedOnDownloadedJar() throws DetectorException {
        final File dockerInspectorJar = findOrDownloadJar();
        final DockerInspectorInfo info = getInfoBasedOnJar(dockerInspectorJar);
        return info;
    }

    private DockerInspectorInfo getInfoBasedOnJar(final File dockerInspectorJar) {
        DockerInspectorInfo info = null;
        if (dockerInspectorJar != null) {
            info = new DockerInspectorInfo(dockerInspectorJar);
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

    private File getConfiguredJar() {
        logger.debug("Checking for user-specified disk-resident docker inspector jar file");
        File providedJar = null;
        final String providedJarPath = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_PATH, PropertyAuthority.None);
        if (StringUtils.isNotBlank(providedJarPath)) {
            logger.debug(String.format("Using user-provided docker inspector jar path: %s", providedJarPath));
            final File providedJarCandidate = new File(providedJarPath);
            if (providedJarCandidate.isFile()) {
                logger.debug(String.format("Found user-specified jar: %s", providedJarCandidate.getAbsolutePath()));
                providedJar = providedJarCandidate;
            }
        }
        return providedJar;
    }

    private File getAirGapJar() {
        final String airGapDirPath = airGapManager.getDockerInspectorAirGapPath();
        logger.debug(String.format("Checking for air gap docker inspector jar file in %s", airGapDirPath));
        try {
            final File airGapJarFile = detectFileFinder.findFilesToDepth(airGapDirPath, "*.jar", 1).get(0);
            logger.debug(String.format("Found air gap jar: %s", airGapJarFile.getAbsolutePath()));
            return airGapJarFile;
        } catch (final Exception e) {
            logger.debug(String.format("Did not find a docker inspector jar file in the airgap dir %s", airGapDirPath));
            return null;
        }
    }

    private File findOrDownloadJar() throws DetectorException {
        String dockerVersion = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_VERSION, PropertyAuthority.None);
        Optional<String> location = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.DOCKER_INSPECTOR_REPO, ArtifactoryConstants.DOCKER_INSPECTOR_PROPERTY, dockerVersion,
            ArtifactoryConstants.DOCKER_INSPECTOR_VERSION_OVERRIDE);
        if (location.isPresent()) {
            File dockerDirectory = directoryManager.getSharedDirectory(DOCKER_SHARED_DIRECTORY_NAME);
            Optional<File> jarFile = artifactResolver.downloadOrFindArtifact(dockerDirectory, location.get());
            if (jarFile.isPresent()) {
                return jarFile.get();
            } else {
                throw new DetectorException("Unable to download of find Docker jar from artifactory.");
            }
        } else {
            throw new DetectorException("Unable to find Docker version from artifactory.");
        }
    }
}
