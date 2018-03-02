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
package com.blackducksoftware.integration.hub.detect.bomtool.docker

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DockerProperties {
    @Autowired
    DetectConfiguration detectConfiguration

    public void populatePropertiesFile(File dockerPropertiesFile, File bomToolOutputDirectory) {
        Properties dockerProperties = new Properties()

        dockerProperties.setProperty('logging.level.com.blackducksoftware', detectConfiguration.getLoggingLevel())
        dockerProperties.setProperty('upload.bdio', 'false')
        dockerProperties.setProperty('no.prompt', 'true')
        dockerProperties.setProperty('output.path', bomToolOutputDirectory.getAbsolutePath())
        dockerProperties.setProperty('output.include.containerfilesystem', 'true')
        dockerProperties.setProperty('logging.level.com.blackducksoftware', detectConfiguration.getLoggingLevel())
        dockerProperties.setProperty('phone.home', 'false')

        detectConfiguration.additionalDockerPropertyNames.each { propertyName ->
            String dockerKey = getKeyWithoutPrefix(propertyName, DetectConfiguration.DOCKER_PROPERTY_PREFIX)
            addDockerProperty(dockerProperties, propertyName, dockerKey)
        }

        dockerProperties.store(dockerPropertiesFile.newOutputStream(), "")
    }

    public void populateEnvironmentVariables(Map<String, String> environmentVariables, String dockerExecutablePath) {
        String path = System.getenv('PATH')
        File dockerExecutableFile = new File(dockerExecutablePath)
        path += File.pathSeparator + dockerExecutableFile.parentFile.getCanonicalPath()
        environmentVariables.put('PATH', path)
        environmentVariables.put('DOCKER_INSPECTOR_VERSION', detectConfiguration.dockerInspectorVersion)

        String detectCurlOpts = System.getenv('DETECT_CURL_OPTS')
        if (StringUtils.isNotBlank(detectCurlOpts)) {
            environmentVariables.put('DOCKER_INSPECTOR_CURL_OPTS', detectCurlOpts)
        }

        environmentVariables.put('BLACKDUCK_HUB_PROXY_HOST', detectConfiguration.hubProxyHost)
        environmentVariables.put('BLACKDUCK_HUB_PROXY_PORT', detectConfiguration.hubProxyPort)
        environmentVariables.put('BLACKDUCK_HUB_PROXY_USERNAME', detectConfiguration.hubProxyUsername)
        environmentVariables.put('BLACKDUCK_HUB_PROXY_PASSWORD', detectConfiguration.hubProxyPassword)

        for (Map.Entry<String, String> environmentProperty : System.getenv()) {
            String key = environmentProperty.getKey()
            if (key != null && key.startsWith(DetectConfiguration.DOCKER_ENVIRONMENT_PREFIX)) {
                environmentVariables.put(getKeyWithoutPrefix(key, DetectConfiguration.DOCKER_ENVIRONMENT_PREFIX), environmentProperty.getValue())
            }
        }
    }

    private String getKeyWithoutPrefix(String key, String prefix) {
        return key[prefix.length()..-1]
    }

    private String addDockerProperty(Properties dockerProperties, String key, String dockerKey) {
        String value = detectConfiguration.getDetectProperty(key)
        dockerProperties.setProperty(dockerKey, value)
    }
}