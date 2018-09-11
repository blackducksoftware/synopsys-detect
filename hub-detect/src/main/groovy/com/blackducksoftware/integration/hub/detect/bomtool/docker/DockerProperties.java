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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertySource;

public class DockerProperties {
    private final DetectConfiguration detectConfiguration;

    public DockerProperties(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public void populatePropertiesFile(final File dockerPropertiesFile, final File outputDirectory) throws IOException {
        final Properties dockerProperties = new Properties();

        dockerProperties.setProperty("logging.level.com.blackducksoftware", detectConfiguration.getProperty(DetectProperty.LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION));
        dockerProperties.setProperty("upload.bdio", "false");
        dockerProperties.setProperty("output.path", outputDirectory.getAbsolutePath());
        dockerProperties.setProperty("output.include.containerfilesystem", "true");
        dockerProperties.setProperty("phone.home", "false");

        final Map<String, String> additionalDockerProperties = detectConfiguration.getDockerProperties();
        additionalDockerProperties.forEach((key, value) -> dockerProperties.setProperty(key, value));

        dockerProperties.store(new FileOutputStream(dockerPropertiesFile), "");
    }

    public void populateEnvironmentVariables(final String dockerInspectorVersion, final Map<String, String> environmentVariables, final File dockerExecutableFile) throws IOException {
        String path = System.getenv("PATH");
        if (dockerExecutableFile != null && dockerExecutableFile.exists()) {
            path += File.pathSeparator + dockerExecutableFile.getParentFile().getCanonicalPath();
        }
        environmentVariables.put("PATH", path);

        if (StringUtils.isNotBlank(dockerInspectorVersion)) {
            environmentVariables.put("DOCKER_INSPECTOR_VERSION", dockerInspectorVersion);
        }

        final String detectCurlOpts = System.getenv("DETECT_CURL_OPTS");
        if (StringUtils.isNotBlank(detectCurlOpts)) {
            environmentVariables.put("DOCKER_INSPECTOR_CURL_OPTS", detectCurlOpts);
        }

        environmentVariables.put("BLACKDUCK_HUB_PROXY_HOST", detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_HOST));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_PORT", detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_PORT));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_USERNAME", detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_USERNAME));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_PASSWORD", detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_PASSWORD));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_IGNORED_HOSTS", detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_IGNORED_HOSTS));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_NTLM_DOMAIN", detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_DOMAIN));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION", detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_WORKSTATION));

        final Map<String, String> additionalDockerProperties = detectConfiguration.getDockerEnvironmentProperties();
        additionalDockerProperties.forEach((key, value) -> environmentVariables.put(key, value));

    }

}
