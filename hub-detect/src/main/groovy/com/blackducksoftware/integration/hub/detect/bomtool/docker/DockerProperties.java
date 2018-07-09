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

import com.blackducksoftware.integration.hub.detect.configuration.AdditionalPropertyConfig;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;

public class DockerProperties {
    private final DetectConfigWrapper detectConfigWrapper;
    private final AdditionalPropertyConfig additionalPropertyConfig;

    public DockerProperties(final DetectConfigWrapper detectConfigWrapper, final AdditionalPropertyConfig additionalPropertyConfig) {
        this.detectConfigWrapper = detectConfigWrapper;
        this.additionalPropertyConfig = additionalPropertyConfig;
    }

    public void populatePropertiesFile(final File dockerPropertiesFile, final File outputDirectory) throws IOException {
        final Properties dockerProperties = new Properties();

        dockerProperties.setProperty("logging.level.com.blackducksoftware", detectConfigWrapper.getProperty(DetectProperty.LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE_INTEGRATION));
        dockerProperties.setProperty("upload.bdio", "false");
        dockerProperties.setProperty("output.path", outputDirectory.getAbsolutePath());
        dockerProperties.setProperty("output.include.containerfilesystem", "true");
        dockerProperties.setProperty("phone.home", "false");

        for (final String additionalProperty : additionalPropertyConfig.getAdditionalDockerPropertyNames()) {
            final String dockerKey = getKeyWithoutPrefix(additionalProperty, AdditionalPropertyConfig.DOCKER_PROPERTY_PREFIX);
            addDockerProperty(dockerProperties, additionalProperty, dockerKey);
        }

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

        environmentVariables.put("BLACKDUCK_HUB_PROXY_HOST", detectConfigWrapper.getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_HOST));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_PORT", detectConfigWrapper.getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_PORT));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_USERNAME", detectConfigWrapper.getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_USERNAME));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_PASSWORD", detectConfigWrapper.getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_PASSWORD));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_IGNORED_HOSTS", detectConfigWrapper.getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_IGNORED_HOSTS));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_NTLM_DOMAIN", detectConfigWrapper.getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_NTLM_DOMAIN));
        environmentVariables.put("BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION", detectConfigWrapper.getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION));

        for (final Map.Entry<String, String> environmentProperty : System.getenv().entrySet()) {
            final String key = environmentProperty.getKey();
            if (key != null && key.startsWith(AdditionalPropertyConfig.DOCKER_ENVIRONMENT_PREFIX)) {
                environmentVariables.put(getKeyWithoutPrefix(key, AdditionalPropertyConfig.DOCKER_ENVIRONMENT_PREFIX), environmentProperty.getValue());
            }
        }
    }

    private String getKeyWithoutPrefix(final String key, final String prefix) {
        return key.substring(prefix.length());
    }

    private void addDockerProperty(final Properties dockerProperties, final String key, final String dockerKey) {
        final String value = additionalPropertyConfig.getDetectProperty(key);
        dockerProperties.setProperty(dockerKey, value);
    }

}
