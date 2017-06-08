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

import javax.annotation.PostConstruct

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectProperties

@Component
class DockerProperties {
    @Autowired
    DetectProperties detectProperties

    private static final String HUB_URL = "hub.url"
    private static final String HUB_TIMEOUT = "hub.timeout"
    private static final String HUB_USERNAME = "hub.username"
    private static final String HUB_PASSWORD = "hub.password"
    private static final String HUB_PROXY_HOST = "hub.proxy.host"
    private static final String HUB_PROXY_PORT = "hub.proxy.port"
    private static final String HUB_PROXY_USERNAME = "hub.proxy.username"
    private static final String HUB_PROXY_PASSWORD = "hub.proxy.password"
    private static final String HUB_PROJECT_NAME = "hub.project.name"
    private static final String HUB_PROJECT_VERSION = "hub.project.version"
    private static final String DOCKER_TAR = "docker.tar"
    private static final String DOCKER_IMAGE = "docker.image"
    private static final String INSTALL_DIR = "install.dir"
    private static final String WORKING_DIRECTORY = "working.directory"
    private static final String LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE = "logging.level.com.blackducksoftware"

    @PostConstruct
    void init() {
        List<String> args = createDockerArgumentList()
        args.each { println it }
    }

    List<String> createDockerArgumentList() {
        def arguments = []
        constructArgument(arguments, 'detect.hub.url', HUB_URL)
        constructArgument(arguments, 'detect.hub.timeout', HUB_TIMEOUT)
        constructArgument(arguments, 'detect.hub.username', HUB_USERNAME)
        constructArgument(arguments, 'detect.hub.password', HUB_PASSWORD)
        constructArgument(arguments, 'detect.hub.proxy.host', HUB_PROXY_HOST)
        constructArgument(arguments, 'detect.hub.proxy.port', HUB_PROXY_PORT)
        constructArgument(arguments, 'detect.hub.proxy.username', HUB_PROXY_USERNAME)
        constructArgument(arguments, 'detect.hub.proxy.password', HUB_PROXY_PASSWORD)
        constructArgument(arguments, 'detect.project.name', HUB_PROJECT_NAME)
        constructArgument(arguments, 'detect.project.version', HUB_PROJECT_VERSION)
        constructArgument(arguments, 'detect.docker.tar', DOCKER_TAR)
        constructArgument(arguments, 'detect.docker.image', DOCKER_IMAGE)
        constructArgument(arguments, 'detect.docker.install.path', INSTALL_DIR)
        constructArgument(arguments, 'detect.docker.sandbox.path', WORKING_DIRECTORY)
        constructArgument(arguments, 'logging.level.com.blackducksoftware.integration', LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE)

        detectProperties.additionalDockerPropertyNames.each { propertyName ->
            String dockerKey = propertyName[DetectProperties.DOCKER_PROPERTY_PREFIX.length()..-1]
            constructArgument(arguments, propertyName, dockerKey)
        }

        arguments
    }

    private String constructArgument(List<String> arguments, String key, String dockerKey) {
        String value = detectProperties.getDetectProperty(key)
        if (value) {
            arguments.add("--${dockerKey}=${value}")
        }
    }
}