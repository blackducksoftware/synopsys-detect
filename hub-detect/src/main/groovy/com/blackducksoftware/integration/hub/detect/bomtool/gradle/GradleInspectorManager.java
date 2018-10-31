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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.MavenMetadataService;
import com.synopsys.integration.exception.IntegrationException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GradleInspectorManager {
    private static final String DEFAULT_GRADLE_INSPECTOR_REPO_URL = "https://test-repo.blackducksoftware.com/artifactory/bds-integrations-release/";
    private static final String VERSION_METADATA_XML_FILENAME = "maven-metadata.xml";
    private static final String RELATIVE_PATH_TO_VERSION_METADATA = "com/blackducksoftware/integration/integration-gradle-inspector/" + VERSION_METADATA_XML_FILENAME;

    private static final String GRADLE_DIR_NAME = "gradle";
    private static final String GRADLE_SCRIPT_TEMPLATE_FILENAME = "init-script-gradle.ftl";
    private static final String GENERATED_GRADLE_SCRIPT_NAME = "init-detect.gradle";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectFileManager detectFileManager;
    private final Configuration configuration;
    private final DetectConfiguration detectConfiguration;
    private final MavenMetadataService mavenMetadataService;

    private String generatedGradleScriptPath = null;
    private boolean hasResolvedInspector = false;

    public GradleInspectorManager(final DetectFileManager detectFileManager, final Configuration configuration, final DetectConfiguration detectConfiguration, final MavenMetadataService mavenMetadataService) {
        this.detectFileManager = detectFileManager;
        this.configuration = configuration;
        this.detectConfiguration = detectConfiguration;
        this.mavenMetadataService = mavenMetadataService;
    }

    public String getGradleInspector() throws BomToolException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            final String resolvedVersion = resolveInspectorVersion();
            try {
                generatedGradleScriptPath = generateGradleScript(resolvedVersion);
            } catch (final Exception e) {
                throw new BomToolException(e);
            }
        }
        return generatedGradleScriptPath;
    }

    private String resolveInspectorVersion() {
        final String versionRange = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_VERSION);
        String gradleInspectorVersion = null;
        try {
            Document xmlDocument = null;
            final File airGapMavenMetadataFile = new File(detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH), VERSION_METADATA_XML_FILENAME);
            if (airGapMavenMetadataFile.exists()) {
                xmlDocument = mavenMetadataService.fetchXmlDocumentFromFile(airGapMavenMetadataFile);
            } else {
                final String mavenMetadataUrl = deriveMavenMetadataUrl();
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
        return gradleInspectorVersion;
    }

    private String deriveMavenMetadataUrl() {
        final String mavenMetadataUrl;
        final String configuredGradleInspectorRepositoryUrl = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL);
        if (StringUtils.isNotBlank(configuredGradleInspectorRepositoryUrl)) {
            if (configuredGradleInspectorRepositoryUrl.endsWith("/")) {
                mavenMetadataUrl = configuredGradleInspectorRepositoryUrl + RELATIVE_PATH_TO_VERSION_METADATA;
            } else {
                mavenMetadataUrl = configuredGradleInspectorRepositoryUrl + "/" + RELATIVE_PATH_TO_VERSION_METADATA;
            }
        } else {
            mavenMetadataUrl = DEFAULT_GRADLE_INSPECTOR_REPO_URL + RELATIVE_PATH_TO_VERSION_METADATA;
        }
        return mavenMetadataUrl;
    }

    private String generateGradleScript(final String version) throws IOException, TemplateException {
        final File generatedGradleScriptFile = detectFileManager.createSharedFile(GRADLE_DIR_NAME, GENERATED_GRADLE_SCRIPT_NAME);
        final Map<String, String> gradleScriptData = new HashMap<>();
        gradleScriptData.put("gradleInspectorVersion", version);
        gradleScriptData.put("excludedProjectNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_EXCLUDED_PROJECTS));
        gradleScriptData.put("includedProjectNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INCLUDED_PROJECTS));
        gradleScriptData.put("excludedConfigurationNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS));
        gradleScriptData.put("includedConfigurationNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INCLUDED_CONFIGURATIONS));
        addReposToGradleScriptData(gradleScriptData);
        populateGradleScriptWithData(generatedGradleScriptFile, gradleScriptData);
        return generatedGradleScriptFile.getCanonicalPath();
    }

    private void addReposToGradleScriptData(Map<String, String> gradleScriptData) {
        try {
            final File gradleInspectorAirGapDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH));
            if (gradleInspectorAirGapDirectory.exists()) {
                gradleScriptData.put("airGapLibsPath", StringEscapeUtils.escapeJava(gradleInspectorAirGapDirectory.getCanonicalPath()));
                return;
            }
        } catch (final Exception e) {
            logger.debug(String.format("Exception encountered when resolving air gap path for gradle, running in online mode instead: %s", e.getMessage()));
        }
        gradleScriptData.put("integrationRepositoryUrl", DEFAULT_GRADLE_INSPECTOR_REPO_URL);
        final String configuredGradleInspectorRepositoryUrl = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL);
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
}
