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

import org.apache.commons.lang3.StringUtils
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

    private String dockerExecutablePath
    private String bashExecutablePath

    @Override
    public BomToolType getBomToolType() {
        BomToolType.DOCKER
    }

    @Override
    public boolean isBomToolApplicable() {
        dockerExecutablePath = findDockerExecutable()
        if (!dockerExecutablePath) {
            logger.debug('Could not find docker on the environment PATH')
        }
        bashExecutablePath = findBashExecutable()
        if (!bashExecutablePath) {
            logger.debug('Could not find bash on the environment PATH')
        }
        boolean propertiesOk = detectConfiguration.dockerInspectorVersion && (detectConfiguration.dockerTar || detectConfiguration.dockerImage)
        if (!propertiesOk) {
            logger.debug('The docker properties are not sufficient to run')
        }

        dockerExecutablePath && propertiesOk
    }

    @Override
    public List<DependencyNode> extractDependencyNodes() {
        File dockerInstallDirectory = new File(detectConfiguration.dockerInstallPath)
        File shellScriptFile
        if (detectConfiguration.dockerInspectorPath) {
            shellScriptFile = new File(detectConfiguration.dockerInspectorPath)
        } else {
            URL hubDockerInspectorShellScriptUrl = new URL("https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector-${detectConfiguration.dockerInspectorVersion}.sh")
            String shellScriptContents = hubDockerInspectorShellScriptUrl.openStream().getText(StandardCharsets.UTF_8.name())
            shellScriptFile = new File(dockerInstallDirectory, "hub-docker-inspector-${detectConfiguration.dockerInspectorVersion}.sh")
            detectFileManager.writeToFile(shellScriptFile, shellScriptContents)
            shellScriptFile.setExecutable(true)
        }

        String path = System.getenv('PATH')
        File dockerExecutableFile = new File(dockerExecutablePath)
        path += File.pathSeparator + dockerExecutableFile.parentFile.absolutePath
        Map<String, String> environmentVariables = [PATH: path]

        List<String> dockerShellScriptArguments = dockerProperties.createDockerArgumentList()
        String bashScriptArg = StringUtils.join(dockerShellScriptArguments, ' ')

        List<String> bashArguments = [
            "-c",
            "${shellScriptFile.absolutePath} ${bashScriptArg}"
        ]

        Executable dockerExecutable = new Executable(dockerInstallDirectory, environmentVariables, bashExecutablePath, bashArguments)
        executableRunner.execute(dockerExecutable)
        //At least for the moment, there is no way of running the hub-docker-inspector to generate the files only, so it currently handles all uploading
        return []
    }

    private String findDockerExecutable() {
        String dockerPath = detectConfiguration.dockerPath
        if (!dockerPath?.trim()) {
            dockerPath = executableManager.getPathOfExecutable(ExecutableType.DOCKER)?.trim()
        }
        dockerPath
    }

    private String findBashExecutable() {
        String bashPath = detectConfiguration.bashPath
        if (!bashPath?.trim()) {
            bashPath = executableManager.getPathOfExecutable(ExecutableType.BASH)?.trim()
        }
        bashPath
    }
}
