/*
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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle

import javax.xml.parsers.DocumentBuilder

import org.apache.commons.text.StringEscapeUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolInspectorManager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.request.Request
import com.blackducksoftware.integration.hub.request.Response
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection

import freemarker.template.Configuration
import freemarker.template.Template
import groovy.transform.TypeChecked

@Component
@TypeChecked
class GradleInspectorManager extends BomToolInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(GradleInspectorManager.class)

    @Autowired
    Configuration configuration

    @Autowired
    DocumentBuilder xmlDocumentBuilder

    private String inspectorVersion
    private String initScriptPath

    public BomToolType getBomToolType() {
        return BomToolType.GRADLE;
    }

    public void install() {
        inspectorVersion = resolveInspectorVersion();
        initScriptPath = resolveInitScriptPath(inspectorVersion);
    }

    public String getInspectorVersion() {
        return inspectorVersion;
    }

    public String getInitScriptPath() {
        return initScriptPath;
    }

    String resolveInspectorVersion() {
        if ('latest'.equalsIgnoreCase(detectConfiguration.getGradleInspectorVersion())) {
            try {
                Document xmlDocument = null
                File airGapMavenMetadataFile = new File(detectConfiguration.getGradleInspectorAirGapPath(), 'maven-metadata.xml')
                if (airGapMavenMetadataFile.exists()) {
                    InputStream inputStream = new FileInputStream(airGapMavenMetadataFile)
                    xmlDocument = xmlDocumentBuilder.parse(inputStream)
                } else {
                    String mavenMetadataUrl = 'http://repo2.maven.org/maven2/com/blackducksoftware/integration/integration-gradle-inspector/maven-metadata.xml'
                    UnauthenticatedRestConnection restConnection = detectConfiguration.createUnauthenticatedRestConnection(mavenMetadataUrl)
                    Request request = new Request.Builder().uri(mavenMetadataUrl).build();
                    Response response = null
                    try {
                        response = restConnection.executeRequest(request)
                        InputStream inputStream = response.getContent()
                        xmlDocument = xmlDocumentBuilder.parse(inputStream)
                    } finally {
                        if ( null != response) {
                            response.close()
                        }
                    }
                }
                final NodeList latestVersionNodes = xmlDocument.getElementsByTagName('latest')
                final Node latestVersion = latestVersionNodes.item(0)
                def inspectorVersion = latestVersion.getTextContent()
                logger.info("Resolved gradle inspector version from latest to: ${inspectorVersion}")
                return inspectorVersion;
            } catch (Exception e) {
                def inspectorVersion = detectConfiguration.getGradleInspectorVersion()
                logger.debug('Exception encountered when resolving latest version of Gradle Inspector, skipping resolution.')
                logger.debug(e.getMessage())
                return inspectorVersion;
            }
        } else {
            return detectConfiguration.getGradleInspectorVersion()
        }
    }

    String resolveInitScriptPath(String inspectorVersion) {

        File initScriptFile = detectFileManager.createFile(BomToolType.GRADLE, 'init-detect.gradle')
        final Map<String, String> model = [
            'gradleInspectorVersion' : inspectorVersion,
            'excludedProjectNames' : detectConfiguration.getGradleExcludedProjectNames(),
            'includedProjectNames' : detectConfiguration.getGradleIncludedProjectNames(),
            'excludedConfigurationNames' : detectConfiguration.getGradleExcludedConfigurationNames(),
            'includedConfigurationNames' : detectConfiguration.getGradleIncludedConfigurationNames()
        ]

        try {
            def gradleInspectorAirGapDirectory = new File(detectConfiguration.getGradleInspectorAirGapPath())
            if (gradleInspectorAirGapDirectory.exists()) {
                model.put('airGapLibsPath', StringEscapeUtils.escapeJava(gradleInspectorAirGapDirectory.getCanonicalPath()))
            }
        } catch (Exception e) {
            logger.debug('Exception encountered when resolving air gap path for gradle, running in online mode instead')
            logger.debug(e.getMessage())
        }

        if (detectConfiguration.getGradleInspectorRepositoryUrl()) {
            model.put('customRepositoryUrl', detectConfiguration.getGradleInspectorRepositoryUrl())
        }

        final Template initScriptTemplate = configuration.getTemplate('init-script-gradle.ftl')
        initScriptFile.withWriter('UTF-8') {
            initScriptTemplate.process(model, it)
        }

        return initScriptFile.getCanonicalPath()
    }
}
