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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.util.MavenMetadataService;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapManager;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;
import com.synopsys.integration.util.ResourceUtil;

public class DockerInspectorManager {
    private static final String IMAGE_INSPECTOR_FAMILY = "blackduck-imageinspector-ws";
    private static final String ARTIFACTORY_URL_BASE = "https://repo.blackducksoftware.com/artifactory/bds-integrations-release/com/synopsys/integration/blackduck-docker-inspector/";
    private static final String ARTIFACTORY_URL_METADATA = ARTIFACTORY_URL_BASE + "maven-metadata.xml";
    private static final String ARTIFACTORY_URL_JAR_PATTERN = ARTIFACTORY_URL_BASE + "%s/%s";
    private static final List<String> inspectorNames = Arrays.asList("ubuntu", "alpine", "centos");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String dockerSharedDirectoryName = "docker";

    private final DirectoryManager directoryManager;
    private final AirGapManager airGapManager;
    private final DetectFileFinder detectFileFinder;
    private final DetectConfiguration detectConfiguration;
    private final ConnectionManager connectionManager;
    private final MavenMetadataService mavenMetadataService;

    private DockerInspectorInfo resolvedInfo;
    private boolean hasResolvedInspector;

    public DockerInspectorManager(final DirectoryManager directoryManager, AirGapManager airGapManager, final DetectFileFinder detectFileFinder,
        final DetectConfiguration detectConfiguration, final ConnectionManager connectionManager, final MavenMetadataService mavenMetadataService) {
        this.directoryManager = directoryManager;
        this.airGapManager = airGapManager;
        this.detectFileFinder = detectFileFinder;
        this.detectConfiguration = detectConfiguration;
        this.connectionManager = connectionManager;
        this.mavenMetadataService = mavenMetadataService;
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

    private DockerInspectorInfo install() throws DetectUserFriendlyException {
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

    private DockerInspectorInfo getInfoBasedOnDownloadedJar() throws DetectUserFriendlyException {
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

    private String getJarFilename(final String version) {
        final String jarFilename = String.format("blackduck-docker-inspector-%s.jar", version);
        return jarFilename;
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

    private File findOrDownloadJar() throws DetectUserFriendlyException {
        logger.debug("Looking for / downloading docker inspector jar file");
        final String resolvedVersion = resolveInspectorVersion();
        final String jarFilename = getJarFilename(resolvedVersion);
        final File inspectorDirectory = directoryManager.getSharedDirectory(dockerSharedDirectoryName);
        final File jarFile = new File(inspectorDirectory, jarFilename);
        if (jarFile.exists()) {
            logger.debug(String.format("Found previously-downloaded docker inspector jar file %s", jarFile.getAbsolutePath()));
        } else {
            downloadJar(resolvedVersion, jarFile);
        }
        return jarFile;
    }

    private void downloadJar(final String resolvedVersion, final File jarFile) throws DetectUserFriendlyException {
        final String hubDockerInspectorJarUrl = String.format(ARTIFACTORY_URL_JAR_PATTERN, resolvedVersion, jarFile.getName());
        logger.debug(String.format("Downloading docker inspector jar file from %s to %s", hubDockerInspectorJarUrl, jarFile.getAbsolutePath()));
        final Request request = new Request.Builder().uri(hubDockerInspectorJarUrl).build();
        Response response = null;
        try (final UnauthenticatedRestConnection restConnection = connectionManager.createUnauthenticatedRestConnection(hubDockerInspectorJarUrl)) {
            response = restConnection.executeRequest(request);
            final InputStream jarBytesInputStream = response.getContent();
            jarFile.delete();
            FileUtils.copyInputStreamToFile(jarBytesInputStream, jarFile);
        } catch (IntegrationException | IOException e) {
            throw new DetectUserFriendlyException(String.format("There was a problem retrieving the docker inspector shell script from %s: %s", hubDockerInspectorJarUrl, e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } finally {
            ResourceUtil.closeQuietly(response);
        }
        logger.debug(String.format("Downloaded docker inspector jar: %s", jarFile.getAbsolutePath()));
    }

    private String resolveInspectorVersion() throws DetectUserFriendlyException {
        final String configuredVersionRangeSpec = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_VERSION, PropertyAuthority.None);
        try {
            return selectArtifactoryVersion(configuredVersionRangeSpec);
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("Unable to find docker inspector version matching configured version range %s", configuredVersionRangeSpec), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    private String selectArtifactoryVersion(final String versionRange) throws IOException, DetectUserFriendlyException, SAXException, IntegrationException {
        logger.trace(String.format("selectArtifactorVersion(): given version range: %s", versionRange));
        final String mavenMetadataUrl = ARTIFACTORY_URL_METADATA;
        final Document xmlDocument = mavenMetadataService.fetchXmlDocumentFromUrl(mavenMetadataUrl);
        final Optional<String> version = mavenMetadataService.parseVersionFromXML(xmlDocument, versionRange);
        logger.trace(String.format("selectArtifactorVersion(): parsed version: %s", version.get()));
        return version.orElse(versionRange);
    }
}
