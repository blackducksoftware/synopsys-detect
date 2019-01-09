/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.detector.gradle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.workflow.ArtifactoryConstants;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GradleScriptCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String GRADLE_SCRIPT_TEMPLATE_FILENAME = "init-script-gradle.ftl";

    private DetectConfiguration detectConfiguration;
    private Configuration configuration;

    public GradleScriptCreator(final DetectConfiguration detectConfiguration, final Configuration configuration) {

        this.detectConfiguration = detectConfiguration;
        this.configuration = configuration;
    }

    public String generateAirGapScript(File scriptFile, String airGapLibs) throws IOException, TemplateException {
        return generateGradleScript(scriptFile, airGapLibs, null);
    }

    public String generateOnlineScript(File scriptFile, String inspectorVersion) throws IOException, TemplateException {
        return generateGradleScript(scriptFile, null, inspectorVersion);
    }

    //You must provide EITHER an airGapLibs OR an inspectorVersion.
    private String generateGradleScript(File scriptFile, String airGapLibs, String inspectorVersion) throws IOException, TemplateException {
        logger.debug("Generating the gradle script file.");
        final Map<String, String> gradleScriptData = new HashMap<>();
        gradleScriptData.put("airGapLibsPath", StringEscapeUtils.escapeJava(airGapLibs));
        gradleScriptData.put("gradleInspectorVersion", StringEscapeUtils.escapeJava(inspectorVersion));
        gradleScriptData.put("excludedProjectNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_EXCLUDED_PROJECTS, PropertyAuthority.None));
        gradleScriptData.put("includedProjectNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INCLUDED_PROJECTS, PropertyAuthority.None));
        gradleScriptData.put("excludedConfigurationNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_EXCLUDED_CONFIGURATIONS, PropertyAuthority.None));
        gradleScriptData.put("includedConfigurationNames", detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INCLUDED_CONFIGURATIONS, PropertyAuthority.None));
        final String configuredGradleInspectorRepositoryUrl = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_REPOSITORY_URL, PropertyAuthority.None);
        String customRepository = ArtifactoryConstants.GRADLE_INSPECTOR_MAVEN_REPO;
        if (StringUtils.isNotBlank(configuredGradleInspectorRepositoryUrl)) {
            logger.warn("Using a custom gradle repository will not be supported in the future.");
            customRepository = configuredGradleInspectorRepositoryUrl;
        }
        gradleScriptData.put("customRepositoryUrl", customRepository);

        populateGradleScriptWithData(scriptFile, gradleScriptData);
        logger.trace(String.format("Successfully created gradle script: %s", scriptFile.getCanonicalPath()));
        return scriptFile.getCanonicalPath();
    }

    private void populateGradleScriptWithData(File generatedGradleScriptFile, Map<String, String> gradleScriptData) throws IOException, TemplateException {
        final Template gradleScriptTemplate = configuration.getTemplate(GRADLE_SCRIPT_TEMPLATE_FILENAME);
        try (final Writer fileWriter = new FileWriter(generatedGradleScriptFile)) {
            gradleScriptTemplate.process(gradleScriptData, fileWriter);
        }
    }
}
