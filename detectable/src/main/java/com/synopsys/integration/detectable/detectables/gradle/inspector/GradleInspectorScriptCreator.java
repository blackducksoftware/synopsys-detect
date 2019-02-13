/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.gradle.inspector;

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

import com.synopsys.integration.detectable.detectable.resolver.ArtifactoryConstants;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GradleInspectorScriptCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String GRADLE_SCRIPT_TEMPLATE_FILENAME = "init-script-gradle.ftl";

    private Configuration configuration;

    public GradleInspectorScriptCreator(final Configuration configuration) {
        this.configuration = configuration;
    }

    public String generateGradleScript(File scriptFile, GradleInspectorScriptOptions scriptOptions) throws IOException, TemplateException {
        logger.debug("Generating the gradle script file.");
        final Map<String, String> gradleScriptData = new HashMap<>();
        gradleScriptData.put("airGapLibsPath", StringEscapeUtils.escapeJava(scriptOptions.getOfflineLibraryPaths().orElse("")));
        gradleScriptData.put("gradleInspectorVersion", StringEscapeUtils.escapeJava(scriptOptions.getOnlineInspectorVersion().orElse("")));
        gradleScriptData.put("excludedProjectNames", scriptOptions.getExcludedProjectNames());
        gradleScriptData.put("includedProjectNames", scriptOptions.getIncludedProjectNames());
        gradleScriptData.put("excludedConfigurationNames", scriptOptions.getExcludedConfigurationNames());
        gradleScriptData.put("includedConfigurationNames", scriptOptions.getIncludedConfigurationNames());
        final String configuredGradleInspectorRepositoryUrl = scriptOptions.getGradleInspectorRepositoryUrl();
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
