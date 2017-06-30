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

@Component
class DockerProperties {
    @Autowired
    DetectConfiguration detectConfiguration

    String fillInDockerProperties(File dockerPropertiesFile) {
        Properties dockerProperties = new Properties()
        addDockerProperty(dockerProperties, 'detect.hub.url', 'hub.url')
        addDockerProperty(dockerProperties, 'detect.hub.timeout', 'hub.timeout')
        addDockerProperty(dockerProperties, 'detect.hub.username', 'hub.username')
        addDockerProperty(dockerProperties, 'detect.hub.password', 'hub.password')
        addDockerProperty(dockerProperties, 'detect.hub.proxy.host', 'hub.proxy.host')
        addDockerProperty(dockerProperties, 'detect.hub.proxy.port', 'hub.proxy.port')
        addDockerProperty(dockerProperties, 'detect.hub.proxy.username', 'hub.proxy.username')
        addDockerProperty(dockerProperties, 'detect.hub.proxy.password', 'hub.proxy.password')
        addDockerProperty(dockerProperties, 'detect.project.name', 'hub.project.name')
        addDockerProperty(dockerProperties, 'detect.project.version.name', 'hub.project.version')
        addDockerProperty(dockerProperties, 'detect.docker.install.path', 'install.dir')
        addDockerProperty(dockerProperties, 'detect.docker.sandbox.path', 'working.directory')
        addDockerProperty(dockerProperties, 'logging.level.com.blackducksoftware.integration', 'logging.level.com.blackducksoftware')

        addDockerProperty(dockerProperties, 'detect.docker.tar', 'docker.tar')
        addDockerProperty(dockerProperties, 'detect.docker.image', 'docker.image')

        detectConfiguration.additionalDockerPropertyNames.each { propertyName ->
            String dockerKey = propertyName[DetectConfiguration.DOCKER_PROPERTY_PREFIX.length()..-1]
            addDockerProperty(dockerProperties, propertyName, dockerKey)
        }
        dockerProperties.store(dockerPropertiesFile.newOutputStream(), "")

        String imageArgument
        if (detectConfiguration.dockerImage) {
            imageArgument = detectConfiguration.dockerImage
        } else {
            imageArgument = detectConfiguration.dockerTar
        }
        return imageArgument
    }

    private String addDockerProperty(Properties dockerProperties, String key, String dockerKey) {
        String value = detectConfiguration.getDetectProperty(key)
        dockerProperties.setProperty(dockerKey, value)
    }
}