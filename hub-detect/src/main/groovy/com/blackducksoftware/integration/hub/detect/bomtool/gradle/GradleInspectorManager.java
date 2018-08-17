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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnection;
import com.blackducksoftware.integration.rest.request.Request;
import com.blackducksoftware.integration.rest.request.Response;
import com.blackducksoftware.integration.util.ResourceUtil;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class GradleInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(GradleInspectorManager.class);

    private final DetectFileManager detectFileManager;
    private final Configuration configuration;
    private final DocumentBuilder xmlDocumentBuilder;
    private final DetectConfiguration detectConfiguration;
    private final DetectConfigurationUtility detectConfigurationUtility;

    private String resolvedInitScript = null;
    private String resolvedVersion = null;
    private boolean hasResolvedInspector = false;

    public GradleInspectorManager(final DetectFileManager detectFileManager, final Configuration configuration, final DocumentBuilder xmlDocumentBuilder,
            final DetectConfiguration detectConfiguration, final DetectConfigurationUtility detectConfigurationUtility) {
        this.detectFileManager = detectFileManager;
        this.configuration = configuration;
        this.xmlDocumentBuilder = xmlDocumentBuilder;
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationUtility = detectConfigurationUtility;
    }

    public String getGradleInspector(final BomToolEnvironment environment) throws BomToolException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            resolvedVersion = resolveInspectorVersion();
            try {
                resolvedInitScript = resolveInitScriptPath(resolvedVersion);
            } catch (final Exception e) {
                throw new BomToolException(e);
            }
        }
        return resolvedInitScript;
    }

    private String resolveInspectorVersion() {
        final String gradleInspectorVersion = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_VERSION);
        if ("latest".equalsIgnoreCase(gradleInspectorVersion)) {
            try {
                Document xmlDocument = null;
                final File airGapMavenMetadataFile = new File(detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH), "maven-metadata.xml");
                if (airGapMavenMetadataFile.exists()) {
                    final InputStream inputStream = new FileInputStream(airGapMavenMetadataFile);
                    xmlDocument = xmlDocumentBuilder.parse(inputStream);
                } else {
                    final String mavenMetadataUrl = "http://repo2.maven.org/maven2/com/blackducksoftware/integration/integration-gradle-inspector/maven-metadata.xml";
                    final Request request = new Request.Builder().uri(mavenMetadataUrl).build();
                    Response response = null;
                    try (UnauthenticatedRestConnection restConnection = detectConfigurationUtility.createUnauthenticatedRestConnection(mavenMetadataUrl)) {
                        response = restConnection.executeRequest(request);
                        final InputStream inputStream = response.getContent();
                        xmlDocument = xmlDocumentBuilder.parse(inputStream);
                    } finally {
                        ResourceUtil.closeQuietly(response);
                    }
                }
                final NodeList latestVersionNodes = xmlDocument.getElementsByTagName("latest");
                final Node latestVersion = latestVersionNodes.item(0);
                final String inspectorVersion = latestVersion.getTextContent();
                logger.info(String.format("Resolved gradle inspector version from latest to: %s", inspectorVersion));
                return inspectorVersion;
            } catch (final Exception e) {
                logger.debug("Exception encountered when resolving latest version of Gradle Inspector, skipping resolution.");
                logger.debug(e.getMessage());
                return gradleInspectorVersion;
            }
        } else {
            return gradleInspectorVersion;
        }
    }

    private String resolveInitScriptPath(final String version) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
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
