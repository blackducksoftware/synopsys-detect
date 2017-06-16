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

    List<String> createDockerArgumentList() {
        def arguments = []
        constructArgument(arguments, 'detect.hub.url', 'hub.url')
        constructArgument(arguments, 'detect.hub.timeout', 'hub.timeout')
        constructArgument(arguments, 'detect.hub.username', 'hub.username')
        constructArgument(arguments, 'detect.hub.password', 'hub.password')
        constructArgument(arguments, 'detect.hub.proxy.host', 'hub.proxy.host')
        constructArgument(arguments, 'detect.hub.proxy.port', 'hub.proxy.port')
        constructArgument(arguments, 'detect.hub.proxy.username', 'hub.proxy.username')
        constructArgument(arguments, 'detect.hub.proxy.password', 'hub.proxy.password')
        constructArgument(arguments, 'detect.project.name', 'hub.project.name')
        constructArgument(arguments, 'detect.project.version.name', 'hub.project.version')
        constructArgument(arguments, 'detect.docker.install.path', 'install.dir')
        constructArgument(arguments, 'detect.docker.sandbox.path', 'working.directory')
        constructArgument(arguments, 'logging.level.com.blackducksoftware.integration', 'logging.level.com.blackducksoftware')

        detectConfiguration.additionalDockerPropertyNames.each { propertyName ->
            String dockerKey = propertyName[DetectConfiguration.DOCKER_PROPERTY_PREFIX.length()..-1]
            constructArgument(arguments, propertyName, dockerKey)
        }

        if (detectConfiguration.dockerImage) {
            arguments.add(detectConfiguration.dockerImage)
        } else {
            arguments.add(detectConfiguration.dockerTar)
        }

        arguments
    }

    private String constructArgument(List<String> arguments, String key, String dockerKey) {
        String value = detectConfiguration.getDetectProperty(key)
        if (value) {
            arguments.add("--${dockerKey}=${value}")
        }
    }
}