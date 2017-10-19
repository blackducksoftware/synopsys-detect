/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

import freemarker.template.Configuration
import freemarker.template.Template
import groovy.transform.TypeChecked

@Component
@TypeChecked
class GradleInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(GradleInspectorManager.class)

    @Autowired
    Configuration configuration

    @Autowired
    DocumentBuilder xmlDocumentBuilder

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    DetectFileManager detectFileManager

    private String inspectorVersion
    private String initScriptPath

    String getInspectorVersion() {
        if ('latest'.equalsIgnoreCase(detectConfiguration.getGradleInspectorVersion())) {
            if (!inspectorVersion) {
                try {
                    InputStream inputStream
                    File airGapMavenMetadataFile = new File(detectConfiguration.getGradleInspectorAirGapPath(), 'maven-metadata.xml')
                    if (airGapMavenMetadataFile.exists()) {
                        inputStream = new FileInputStream(airGapMavenMetadataFile)
                    } else {
                        URL mavenMetadataUrl = new URL('http://repo2.maven.org/maven2/com/blackducksoftware/integration/integration-gradle-inspector/maven-metadata.xml')
                        inputStream = mavenMetadataUrl.openStream()
                    }
                    final Document xmlDocument = xmlDocumentBuilder.parse(inputStream)
                    final NodeList latestVersionNodes = xmlDocument.getElementsByTagName('latest')
                    final Node latestVersion = latestVersionNodes.item(0)
                    inspectorVersion = latestVersion.getTextContent()
                } catch (Exception e) {
                    inspectorVersion = detectConfiguration.getGradleInspectorVersion()
                    logger.debug('Execption encountered when resolving latest version of Gradle Inspector, skipping resolution.')
                    logger.debug(e.getMessage())
                }
            }
        } else {
            inspectorVersion = detectConfiguration.getGradleInspectorVersion()
        }
        inspectorVersion
    }

    String getInitScriptPath() {
        if (!initScriptPath) {
            File initScriptFile = detectFileManager.createFile(BomToolType.GRADLE, 'init-detect.gradle')
            String gradleInspectorVersion = detectConfiguration.getGradleInspectorVersion()
            gradleInspectorVersion = 'latest'.equalsIgnoreCase(gradleInspectorVersion) ? '+' : gradleInspectorVersion
            final Map<String, String> model = [
                'gradleInspectorVersion' : gradleInspectorVersion,
                'excludedProjectNames' : detectConfiguration.getGradleExcludedProjectNames(),
                'includedProjectNames' : detectConfiguration.getGradleIncludedProjectNames(),
                'excludedConfigurationNames' : detectConfiguration.getGradleExcludedConfigurationNames(),
                'includedConfigurationNames' : detectConfiguration.getGradleIncludedConfigurationNames()
            ]

            try {
                def gradleInspectorAirGapDirectory = new File(detectConfiguration.getGradleInspectorAirGapPath())
                if (gradleInspectorAirGapDirectory.exists()) {
                    model.put('airGapLibsPath', gradleInspectorAirGapDirectory.getCanonicalPath())
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

            initScriptPath = initScriptFile.getCanonicalPath()
        }
        return initScriptPath
    }
}
