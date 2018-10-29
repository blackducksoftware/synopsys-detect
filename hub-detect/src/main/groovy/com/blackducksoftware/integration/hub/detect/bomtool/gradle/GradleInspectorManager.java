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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
import java.net.URL;

public class GradleInspectorManager {
    private static final String MAVEN_CENTRAL_BASE_URL = "http://repo2.maven.org/maven2/";
    private static final String GRADLE_INSPECTOR_METADATA_RELATIVE_URL = "com/blackducksoftware/integration/integration-gradle-inspector/maven-metadata.xml";
    private final Logger logger = LoggerFactory.getLogger(GradleInspectorManager.class);

    private final DetectFileManager detectFileManager;
    private final Configuration configuration;
    private final DetectConfiguration detectConfiguration;
    private final MavenMetadataService mavenMetadataService;

    private String resolvedInitScript = null;
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
                resolvedInitScript = resolveInitScriptPath(resolvedVersion);
            } catch (final Exception e) {
                throw new BomToolException(e);
            }
        }
        return resolvedInitScript;
    }

    private String resolveInspectorVersion() {
        final String versionRange = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_VERSION);
        String gradleInspectorVersion = null;

        try {
            Document xmlDocument = null;
            final File airGapMavenMetadataFile = new File(detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH), "maven-metadata.xml");
            if (airGapMavenMetadataFile.exists()) {
                xmlDocument = mavenMetadataService.fetchXmlDocumentFromFile(airGapMavenMetadataFile);
            } else {
                final String mavenMetadataBaseUrlString;
                final String configuredGradleInspectorRepositoryUrl = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL);
                if (StringUtils.isBlank(configuredGradleInspectorRepositoryUrl)) {
                    mavenMetadataBaseUrlString = MAVEN_CENTRAL_BASE_URL;
                    logger.debug(String.format("Gradle Inspector Metadata base url defaulting to %s", mavenMetadataBaseUrlString));
                } else {
                    mavenMetadataBaseUrlString = configuredGradleInspectorRepositoryUrl;
                    logger.debug(String.format("Gradle Inspector Metadata base url overridden to %s", mavenMetadataBaseUrlString));
                }
                URL mavenMetaDataBaseUrl = new URL(mavenMetadataBaseUrlString);
                logger.debug(String.format("mavenMetaDataBaseUrl: %s; relative part: %s", mavenMetaDataBaseUrl.toString(), GRADLE_INSPECTOR_METADATA_RELATIVE_URL));
                URL mavenMetaDataFullUrl = new URL(mavenMetaDataBaseUrl, GRADLE_INSPECTOR_METADATA_RELATIVE_URL);
                logger.debug(String.format("Derived Gradle Inspector Metadata full url: %s", mavenMetaDataFullUrl.toString()));
                xmlDocument = mavenMetadataService.fetchXmlDocumentFromUrl(mavenMetaDataFullUrl.toString());
            }

            final Optional<String> versionFromXML = mavenMetadataService.parseVersionFromXML(xmlDocument, versionRange);
            if (versionFromXML.isPresent()) {
                gradleInspectorVersion = versionFromXML.get();
                logger.info(String.format("Resolved gradle inspector version from [%s] to [%s]", versionRange, gradleInspectorVersion.toString()));
            } else {
                throw new IntegrationException(String.format("Failed to find a version matching [%s] in maven-metadata.xml", versionRange));
            }
        } catch (final IntegrationException | SAXException | IOException | DetectUserFriendlyException e) {
            logger.warn("Exception encountered when resolving latest version of Gradle Inspector, skipping resolution.");
            logger.debug(e.getMessage());
        }

        return gradleInspectorVersion;
    }

    private String resolveInitScriptPath(final String version) throws IOException, TemplateException {
        final File initScriptFile = detectFileManager.createSharedFile("gradle", "init-detect.gradle");
        final Map<String, String> model = new HashMap<>();
        model.put("gradleInspectorVersion", version);
        model.put("excludedProjectNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_EXCLUDED_PROJECTS));
        model.put("includedProjectNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INCLUDED_PROJECTS));
        model.put("excludedConfigurationNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS));
        model.put("includedConfigurationNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INCLUDED_CONFIGURATIONS));

        try {
            final File gradleInspectorAirGapDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH));
            if (gradleInspectorAirGapDirectory.exists()) {
                model.put("airGapLibsPath", StringEscapeUtils.escapeJava(gradleInspectorAirGapDirectory.getCanonicalPath()));
            }
        } catch (final Exception e) {
            logger.debug("Exception encountered when resolving air gap path for gradle, running in online mode instead");
            logger.debug(e.getMessage());
        }

        final String gradleInspectorRepositoryUrl = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL);
        if (StringUtils.isNotBlank(gradleInspectorRepositoryUrl)) {
            model.put("customRepositoryUrl", gradleInspectorRepositoryUrl);
        }
        final Template initScriptTemplate = configuration.getTemplate("init-script-gradle.ftl");

        final Writer fileWriter = new FileWriter(initScriptFile);
        initScriptTemplate.process(model, fileWriter);
        fileWriter.close();

        return initScriptFile.getCanonicalPath();
    }
}
