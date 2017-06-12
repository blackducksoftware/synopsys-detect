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
package com.blackducksoftware.integration.hub.detect.bomtool

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerProperties
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

@Component
class DockerBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(DockerBomTool.class)

    @Autowired
    DockerProperties dockerProperties

    @Override
    public BomToolType getBomToolType() {
        BomToolType.DOCKER
    }

    @Override
    public boolean isBomToolApplicable() {
        def dockerExecutablePath = findDockerExecutable()
        if (!dockerExecutablePath) {
            logger.debug('Could not find docker on the environment PATH')
        }
        boolean propertiesOk = detectProperties.dockerInspectorVersion && (detectProperties.dockerTar || detectProperties.dockerImage)
        if (!propertiesOk) {
            logger.debug('The docker properties are not sufficient to run')
        }

        dockerExecutablePath && propertiesOk
    }

    @Override
    public List<DependencyNode> extractDependencyNodes() {
        URL hubDockerInspectorShellScriptUrl = new URL("https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-${detectProperties.dockerInspectorVersion}.sh")
        File dockerInstallDirectory = new File(detectProperties.dockerInstallPath)
        String shellScriptContents = hubDockerInspectorShellScriptUrl.openStream().getText(StandardCharsets.UTF_8.name())
        File shellScriptFile = new File(dockerInstallDirectory, "hub-docker-${detectProperties.dockerInspectorVersion}.sh")
        shellScriptFile.delete()
        shellScriptFile << shellScriptContents
        shellScriptFile.setExecutable(true)

        File docker = new File(findDockerExecutable())
        Map<String, String> environmentVariables = [PATH: docker.parentFile.absolutePath]

        List<String> dockerShellScriptArguments = dockerProperties.createDockerArgumentList()

        Executable dockerExecutable = new Executable(dockerInstallDirectory, environmentVariables, shellScriptFile.absolutePath, dockerShellScriptArguments)
        executableRunner.executeLoudly(dockerExecutable)
        return null;
    }

    private String findDockerExecutable() {
        String dockerPath = detectProperties.dockerPath
        if (!dockerPath?.trim()) {
            dockerPath = executableManager.getPathOfExecutable(ExecutableType.DOCKER)?.trim()
        }
        dockerPath
    }
}
