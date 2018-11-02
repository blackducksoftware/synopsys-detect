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
package com.blackducksoftware.integration.hub.detect.detector.gradle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
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
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;
import com.synopsys.integration.util.ResourceUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GradleInspectorManager {
    private static final String DEFAULT_GRADLE_INSPECTOR_REPO_URL = "https://repo.blackducksoftware.com/artifactory/bds-integrations-release/";
    private static final String VERSION_METADATA_XML_FILENAME = "maven-metadata.xml";
    private static final String GRADLE_INSPECTOR_PACKAGE_PATH = "com/blackducksoftware/integration/integration-gradle-inspector/";
    private static final String RELATIVE_PATH_TO_VERSION_METADATA = GRADLE_INSPECTOR_PACKAGE_PATH + VERSION_METADATA_XML_FILENAME;
    private static final String JAR_URL_PATTERN = "%s" + GRADLE_INSPECTOR_PACKAGE_PATH + "%s/%s";

    private static final String GRADLE_DIR_NAME = "gradle";
    private static final String GRADLE_SCRIPT_TEMPLATE_FILENAME = "init-script-gradle.ftl";
    private static final String GENERATED_GRADLE_SCRIPT_NAME = "init-detect.gradle";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DirectoryManager directoryManager;
    private final AirGapManager airGapManager;
    private final Configuration configuration;
    private final DetectConfiguration detectConfiguration;
    private final ConnectionManager connectionManager;
    private final MavenMetadataService mavenMetadataService;

    private String generatedGradleScriptPath = null;
    private boolean hasResolvedInspector = false;

    public GradleInspectorManager(final DirectoryManager directoryManager, AirGapManager airGapManager, final Configuration configuration, final DetectConfiguration detectConfiguration, final ConnectionManager connectionManager,
        final MavenMetadataService mavenMetadataService) {
        this.directoryManager = directoryManager;
        this.airGapManager = airGapManager;
        this.configuration = configuration;
        this.detectConfiguration = detectConfiguration;
        this.connectionManager = connectionManager;
        this.mavenMetadataService = mavenMetadataService;
    }

    public String getGradleInspector() throws DetectorException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            try {
                final File inspectorDirectory = directoryManager.getSharedDirectory(GRADLE_DIR_NAME);
                String repoBaseUrl = deriveRepoBaseUrl();
                final String resolvedVersion = resolveInspectorVersion(repoBaseUrl);
                String gradleInspectorAirGapDirectoryPath = airGapManager.getGradleInspectorAirGapPath();
                File gradleInspectorAirGapDirectory = deriveGradleAirGapDir(gradleInspectorAirGapDirectoryPath);
                if (gradleInspectorAirGapDirectory == null) {
                    findOrDownloadJar(inspectorDirectory, repoBaseUrl, resolvedVersion);
                }
                generatedGradleScriptPath = generateGradleScript(inspectorDirectory.getCanonicalPath(), gradleInspectorAirGapDirectory, resolvedVersion);
            } catch (final Exception e) {
                throw new DetectorException(e);
            }
        }
        logger.trace(String.format("Derived generated gradle script path: %s", generatedGradleScriptPath));
        return generatedGradleScriptPath;
    }

    private File deriveGradleAirGapDir(String gradleInspectorAirGapDirectoryPath) {
        File gradleInspectorAirGapDirectory = null;
        if (StringUtils.isNotBlank(gradleInspectorAirGapDirectoryPath)) {
            gradleInspectorAirGapDirectory = new File(gradleInspectorAirGapDirectoryPath);
            if (!gradleInspectorAirGapDirectory.exists()) {
                gradleInspectorAirGapDirectory = null;
            }
        }
        logger.trace(String.format("gradleInspectorAirGapDirectory: %s", gradleInspectorAirGapDirectory));
        return gradleInspectorAirGapDirectory;
    }

    private String resolveInspectorVersion(String repoBaseUrl) {
        final String versionRange = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_VERSION, PropertyAuthority.None);
        String gradleInspectorVersion = null;
        try {
            Document xmlDocument = null;
            final File airGapMavenMetadataFile = new File(airGapManager.getGradleInspectorAirGapPath(), VERSION_METADATA_XML_FILENAME);
            if (airGapMavenMetadataFile.exists()) {
                xmlDocument = mavenMetadataService.fetchXmlDocumentFromFile(airGapMavenMetadataFile);
            } else {
                final String mavenMetadataUrl = deriveMavenMetadataUrl(repoBaseUrl);
                xmlDocument = mavenMetadataService.fetchXmlDocumentFromUrl(mavenMetadataUrl);
            }
            final Optional<String> versionFromXML = mavenMetadataService.parseVersionFromXML(xmlDocument, versionRange);
            if (versionFromXML.isPresent()) {
                gradleInspectorVersion = versionFromXML.get();
                logger.info(String.format("Resolved gradle inspector version from [%s] to [%s]", versionRange, gradleInspectorVersion));
            } else {
                throw new IntegrationException(String.format("Failed to find a version matching [%s] in maven-metadata.xml", versionRange));
            }
        } catch (final IntegrationException | SAXException | IOException | DetectUserFriendlyException e) {
            logger.warn(String.format("Exception encountered when resolving latest version of Gradle Inspector, skipping resolution: %s", e.getMessage()));
        }
        logger.trace(String.format("Derived gradle inspector version: %s", gradleInspectorVersion));
        return gradleInspectorVersion;
    }

    private String deriveRepoBaseUrl() {
        final String repoBaseUrl;
        final String configuredGradleInspectorRepositoryUrl = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL, PropertyAuthority.None);
        if (StringUtils.isNotBlank(configuredGradleInspectorRepositoryUrl)) {
            if (configuredGradleInspectorRepositoryUrl.endsWith("/")) {
                repoBaseUrl = configuredGradleInspectorRepositoryUrl;
            } else {
                repoBaseUrl = configuredGradleInspectorRepositoryUrl + "/";
            }
        } else {
            repoBaseUrl = DEFAULT_GRADLE_INSPECTOR_REPO_URL;
        }
        logger.trace(String.format("Derived gradle inspector jar repo base URL: %s", repoBaseUrl));
        return repoBaseUrl;
    }

    private String deriveMavenMetadataUrl(String repoBaseUrl) {
        String mavenMetadataUrl = repoBaseUrl + RELATIVE_PATH_TO_VERSION_METADATA;
        logger.trace(String.format("Derived mavenMetadataUrl: %s", mavenMetadataUrl));
        return mavenMetadataUrl;
    }

    private String generateGradleScript(String inspectorDirPath, File gradleInspectorAirGapDirectory, final String version) throws IOException, TemplateException {
        final File generatedGradleScriptFile = directoryManager.getSharedFile(GRADLE_DIR_NAME, GENERATED_GRADLE_SCRIPT_NAME);
        final Map<String, String> gradleScriptData = new HashMap<>();
        gradleScriptData.put("gradleInspectorVersion", version);
        gradleScriptData.put("excludedProjectNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_EXCLUDED_PROJECTS, PropertyAuthority.None));
        gradleScriptData.put("includedProjectNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INCLUDED_PROJECTS, PropertyAuthority.None));
        gradleScriptData.put("excludedConfigurationNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS, PropertyAuthority.None));
        gradleScriptData.put("includedConfigurationNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INCLUDED_CONFIGURATIONS, PropertyAuthority.None));
        addReposToGradleScriptData(gradleInspectorAirGapDirectory, gradleScriptData, inspectorDirPath);
        populateGradleScriptWithData(generatedGradleScriptFile, gradleScriptData);
        logger.trace(String.format("Derived generatedGradleScriptFile path: %s", generatedGradleScriptFile.getCanonicalPath()));
        return generatedGradleScriptFile.getCanonicalPath();
    }

    private void addReposToGradleScriptData(File gradleInspectorAirGapDirectory, Map<String, String> gradleScriptData, String inspectorDirPath) {
        try {
            if (gradleInspectorAirGapDirectory != null) {
                gradleScriptData.put("airGapLibsPath", StringEscapeUtils.escapeJava(gradleInspectorAirGapDirectory.getCanonicalPath()));
                return; // airgap has everything
            }
        } catch (final Exception e) {
            logger.debug(String.format("Exception encountered when resolving air gap path for gradle, running in online mode instead: %s", e.getMessage()));
        }
        gradleScriptData.put("gradleInspectorDirPath", inspectorDirPath);
        gradleScriptData.put("integrationRepositoryUrl", DEFAULT_GRADLE_INSPECTOR_REPO_URL);
        final String configuredGradleInspectorRepositoryUrl = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL, PropertyAuthority.None);
        if (StringUtils.isNotBlank(configuredGradleInspectorRepositoryUrl)) {
            gradleScriptData.put("customRepositoryUrl", configuredGradleInspectorRepositoryUrl);
        }
    }

    private void populateGradleScriptWithData(File generatedGradleScriptFile, Map<String, String> gradleScriptData) throws IOException, TemplateException {
        final Template gradleScriptTemplate = configuration.getTemplate(GRADLE_SCRIPT_TEMPLATE_FILENAME);
        try (final Writer fileWriter = new FileWriter(generatedGradleScriptFile)) {
            gradleScriptTemplate.process(gradleScriptData, fileWriter);
        }
    }

    private File findOrDownloadJar(File inspectorDirectory, String repoBaseUrlWithTrailingSlash, String jarVersion) throws DetectUserFriendlyException {
        logger.trace("Looking for / downloading gradle inspector jar file");
        final String jarFilename = getJarFilename(jarVersion);
        final File jarFile = new File(inspectorDirectory, jarFilename);
        if (jarFile.exists()) {
            logger.debug(String.format("Found previously-downloaded gradle inspector jar file %s", jarFile.getAbsolutePath()));
        } else {
            downloadJar(repoBaseUrlWithTrailingSlash, jarVersion, jarFile);
        }
        logger.trace(String.format("Found or downloaded jar: %s", jarFile.getAbsolutePath()));
        return jarFile;
    }

    private void downloadJar(String repoBaseUrlWithTrailingSlash, final String jarVersion, final File jarFile) throws DetectUserFriendlyException {
        final String gradleInspectorJarUrl = String.format(JAR_URL_PATTERN, repoBaseUrlWithTrailingSlash, jarVersion, jarFile.getName());
        logger.debug(String.format("Downloading gradle inspector jar file from %s to %s", gradleInspectorJarUrl, jarFile.getAbsolutePath()));
        final Request request = new Request.Builder().uri(gradleInspectorJarUrl).build();
        Response response = null;
        try (final UnauthenticatedRestConnection restConnection = connectionManager.createUnauthenticatedRestConnection(gradleInspectorJarUrl)) {
            response = restConnection.executeRequest(request);
            final InputStream jarBytesInputStream = response.getContent();
            jarFile.delete();
            FileUtils.copyInputStreamToFile(jarBytesInputStream, jarFile);
        } catch (IntegrationException | IOException e) {
            throw new DetectUserFriendlyException(String.format("There was a problem retrieving the gradle inspector shell script from %s: %s", gradleInspectorJarUrl, e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } finally {
            ResourceUtil.closeQuietly(response);
        }
        logger.trace(String.format("Downloaded gradle inspector jar: %s", jarFile.getAbsolutePath()));
    }

    private String getJarFilename(final String version) {
        final String jarFilename = String.format("integration-gradle-inspector-%s.jar", version);
        logger.trace(String.format("Derived jar filename: %s", jarFilename));
        return jarFilename;
    }
}
