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
package com.blackducksoftware.integration.hub.detect.bomtool.docker

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DockerProperties {
    @Autowired
    DetectConfiguration detectConfiguration

    public void fillInDockerProperties(Properties dockerProperties) {
        dockerProperties.setProperty('hub.url', detectConfiguration.getHubUrl())
        dockerProperties.setProperty('hub.timeout', detectConfiguration.getHubTimeout().toString())
        dockerProperties.setProperty('hub.username', detectConfiguration.getHubUsername())
        dockerProperties.setProperty('hub.project.name', detectConfiguration.getProjectName())
        dockerProperties.setProperty('hub.project.version', detectConfiguration.getProjectVersionName())
        dockerProperties.setProperty('logging.level.com.blackducksoftware', detectConfiguration.getLoggingLevel())

        detectConfiguration.additionalDockerPropertyNames.each { propertyName ->
            String dockerKey = propertyName[DetectConfiguration.DOCKER_PROPERTY_PREFIX.length()..-1]
            addDockerProperty(dockerProperties, propertyName, dockerKey)
        }
    }

    public String dockerProxyEnvironmentVariable() {
        String proxyEnvironmentVariable = null
        if (detectConfiguration.getHubProxyHost()) {
            proxyEnvironmentVariable = "-Dhttp.proxy.host=${detectConfiguration.getHubProxyHost()}"
        }
        if (detectConfiguration.getHubProxyPort()) {
            proxyEnvironmentVariable = "${proxyEnvironmentVariable} -Dhttp.proxy.port=${detectConfiguration.getHubProxyPort()}"
        }
        if (detectConfiguration.getHubProxyUsername()) {
            proxyEnvironmentVariable = "${proxyEnvironmentVariable} -Dhttp.proxy.username=${detectConfiguration.getHubProxyUsername()}"
        }
        if (detectConfiguration.getHubProxyPassword()) {
            proxyEnvironmentVariable = "${proxyEnvironmentVariable} -Dhttp.proxy.password=${detectConfiguration.getHubProxyPassword()}"
        }
        proxyEnvironmentVariable
    }

    private String addDockerProperty(Properties dockerProperties, String key, String dockerKey) {
        String value = detectConfiguration.getDetectProperty(key)
        dockerProperties.setProperty(dockerKey, value)
    }
}