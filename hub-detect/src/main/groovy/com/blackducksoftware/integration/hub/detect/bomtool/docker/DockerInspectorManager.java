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
package com.blackducksoftware.integration.hub.detect.bomtool.docker;

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

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.MavenMetadataService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;
import com.synopsys.integration.util.ResourceUtil;

public class DockerInspectorManager {
    private static final String IMAGE_INSPECTOR_FAMILY = "hub-imageinspector-ws";
    private static final String ARTIFACTORY_URL_BASE = "https://test-repo.blackducksoftware.com:443/artifactory/bds-integrations-release/com/blackducksoftware/integration/hub-docker-inspector/";
    private static final String ARTIFACTORY_URL_METADATA = ARTIFACTORY_URL_BASE + "maven-metadata.xml";
    private static final String ARTIFACTORY_URL_JAR_PATTERN = ARTIFACTORY_URL_BASE + "%s/%s";

    private final Logger logger = LoggerFactory.getLogger(DockerInspectorManager.class);

    private final String dockerSharedDirectoryName = "docker";

    private final DetectFileManager detectFileManager;
    private final DetectFileFinder detectFileFinder;
    private final DetectConfiguration detectConfiguration;
    private final DetectConfigurationUtility detectConfigurationUtility;
    private final MavenMetadataService mavenMetadataService;

    public DockerInspectorManager(final DetectFileManager detectFileManager, final DetectFileFinder detectFileFinder,
            final DetectConfiguration detectConfiguration, final DetectConfigurationUtility detectConfigurationUtility, final MavenMetadataService mavenMetadataService) {
        this.detectFileManager = detectFileManager;
        this.detectFileFinder = detectFileFinder;
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationUtility = detectConfigurationUtility;
        this.mavenMetadataService = mavenMetadataService;
    }

    private DockerInspectorInfo resolvedInfo;

    public DockerInspectorInfo getDockerInspector() throws BomToolException {
        logger.trace("*** getDockerInspector() called");
        try {
            if (resolvedInfo == null) {
                install();
            }
            return resolvedInfo;
        } catch (final Exception e) {
            throw new BomToolException(e);
        }
    }

    private void install() throws DetectUserFriendlyException {
        logger.trace("*** install() called");
        boolean offline = false;
        Optional<File> jarFileOptional = getUserSpecifiedDiskResidentJar();
        if (!jarFileOptional.isPresent()) {
            jarFileOptional = getAirGapJar();
            if (jarFileOptional.isPresent()) {
                offline = true;
            } else {
                jarFileOptional = Optional.of(downloadJar());
            }
        }
        List<File> offlineTars = null;
        if (offline) {
            offlineTars = new ArrayList<>();
            final String dockerInspectorAirGapPath = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH);
            for (final String os : Arrays.asList("ubuntu", "alpine", "centos")) {
                final File osImage = new File(dockerInspectorAirGapPath, IMAGE_INSPECTOR_FAMILY + "-" + os + ".tar");
                offlineTars.add(osImage);
            }
        }
        resolvedInfo = new DockerInspectorInfo(jarFileOptional.get(), offlineTars);
    }

    private String getJarFilename(final String version) {
        return String.format("hub-docker-inspector-%s.jar", version);
    }

    private Optional<File> getUserSpecifiedDiskResidentJar() {
        logger.debug("Checking for user-specified disk-resident docker inspector jar file");
        Optional<File> providedJar = Optional.empty();
        final String providedJarPath = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_PATH);
        if (StringUtils.isNotBlank(providedJarPath)) {
            logger.debug(String.format("Using user-provided docker inspector jar path: %s", providedJarPath));
            final File providedJarCandidate = new File(providedJarPath);
            if (providedJarCandidate.isFile()) {
                providedJar = Optional.of(providedJarCandidate);
            }
        }
        return providedJar;
    }

    private Optional<File> getAirGapJar() throws DetectUserFriendlyException {
        logger.debug("Checking for air gap docker inspector jar file");
        final String airGapDirPath = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH);
        try {
            return Optional.of(detectFileFinder.findFilesToDepth(airGapDirPath, "*.jar", 0).get(0));
        } catch (final Exception e) {
            logger.debug(String.format("Did not find a docker inspector jar file in the airgap dir %s (%s)", airGapDirPath, e.getMessage()));
            return Optional.empty();
        }
    }

    private File downloadJar() throws DetectUserFriendlyException {
        logger.debug("Attempting to download docker inspector jar file");
        final String resolvedVersion = resolveInspectorVersion();
        final String jarFilename = this.getJarFilename(resolvedVersion);
        final File inspectorDirectory = detectFileManager.getSharedDirectory(dockerSharedDirectoryName);
        final File jarFile = new File(inspectorDirectory, jarFilename);
        final String hubDockerInspectorJarUrl = String.format(ARTIFACTORY_URL_JAR_PATTERN, resolvedVersion, jarFilename);
        final Request request = new Request.Builder().uri(hubDockerInspectorJarUrl).build();
        Response response = null;
        try (final UnauthenticatedRestConnection restConnection = detectConfigurationUtility.createUnauthenticatedRestConnection(hubDockerInspectorJarUrl)) {
            response = restConnection.executeRequest(request);
            final InputStream jarBytesInputStream = response.getContent();
            // TODO Should we add a method to detectFileManager for this?
            FileUtils.copyInputStreamToFile(jarBytesInputStream, jarFile);
        } catch (IntegrationException | IOException e) {
            throw new DetectUserFriendlyException(String.format("There was a problem retrieving the docker inspector shell script from %s: %s", hubDockerInspectorJarUrl, e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } finally {
            ResourceUtil.closeQuietly(response);
        }
        return jarFile;
    }

    private String resolveInspectorVersion() throws DetectUserFriendlyException {
        final String configuredVersionRangeSpec = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_VERSION);
        try {
            return selectArtifactorVersion(configuredVersionRangeSpec);
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("Unable to find docker inspector version matching configured version range %s", configuredVersionRangeSpec), e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    private String selectArtifactorVersion(final String versionRange) throws IOException, DetectUserFriendlyException, SAXException, IntegrationException {
        final String mavenMetadataUrl = ARTIFACTORY_URL_METADATA;
        final Document xmlDocument = mavenMetadataService.fetchXmlDocumentFromUrl(mavenMetadataUrl);
        final Optional<String> version = mavenMetadataService.parseVersionFromXML(xmlDocument, versionRange);
        return version.orElse(versionRange);
    }
}
