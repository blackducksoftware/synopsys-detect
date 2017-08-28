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

import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerProperties
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

@Component
class DockerBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(DockerBomTool.class)

    static final URL LATEST_URL = new URL('https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector.sh')

    @Autowired
    DockerProperties dockerProperties

    @Autowired
    HubSignatureScanner hubSignatureScanner

    private String dockerExecutablePath
    private String bashExecutablePath

    @Override
    public BomToolType getBomToolType() {
        BomToolType.DOCKER
    }

    @Override
    public boolean isBomToolApplicable() {
        boolean propertiesOk = detectConfiguration.dockerInspectorVersion && (detectConfiguration.dockerTar || detectConfiguration.dockerImage)
        if (!propertiesOk) {
            logger.debug('The docker properties are not sufficient to run')
        } else {
            dockerExecutablePath = getExecutablePath(ExecutableType.DOCKER, true, detectConfiguration.dockerPath)
            bashExecutablePath = getExecutablePath(ExecutableType.BASH, true, detectConfiguration.bashPath)
            if (!dockerExecutablePath) {
                logger.warn("Could not find a ${executableManager.getExecutableName(ExecutableType.DOCKER)} executable")
            }
            if (!bashExecutablePath) {
                logger.warn("Could not find a ${executableManager.getExecutableName(ExecutableType.BASH)} executable")
            }
        }

        dockerExecutablePath && bashExecutablePath && propertiesOk
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        File shellScriptFile
        if (detectConfiguration.dockerInspectorPath) {
            shellScriptFile = new File(detectConfiguration.dockerInspectorPath)
        } else {
            URL hubDockerInspectorShellScriptUrl = LATEST_URL
            if (!'latest'.equals(detectConfiguration.dockerInspectorVersion)) {
                hubDockerInspectorShellScriptUrl = new URL("https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector-${detectConfiguration.dockerInspectorVersion}.sh")
            }
            String shellScriptContents = hubDockerInspectorShellScriptUrl.openStream().getText(StandardCharsets.UTF_8.name())
            shellScriptFile = detectFileManager.createFile(BomToolType.DOCKER, "hub-docker-inspector-${detectConfiguration.dockerInspectorVersion}.sh")
            detectFileManager.writeToFile(shellScriptFile, shellScriptContents)
            shellScriptFile.setExecutable(true)
        }

        File dockerPropertiesFile = detectFileManager.createFile(BomToolType.DOCKER, 'application.properties')
        Properties dockerProps = new Properties()
        dockerProperties.fillInDockerProperties(dockerProps)
        dockerProps.store(dockerPropertiesFile.newOutputStream(), "")

        boolean usingTarFile = false
        String imageArgument = ''
        if (detectConfiguration.dockerImage) {
            imageArgument = detectConfiguration.dockerImage
        } else {
            File dockerTarFile = new File(detectConfiguration.dockerTar)
            imageArgument = dockerTarFile.getCanonicalPath()
            usingTarFile = true
        }

        File dockerPropertiesDirectory =  dockerPropertiesFile.getParentFile()

        String path = System.getenv('PATH')
        File dockerExecutableFile = new File(dockerExecutablePath)
        path += File.pathSeparator + dockerExecutableFile.parentFile.absolutePath
        Map<String, String> environmentVariables = [PATH: path]
        environmentVariables.put('BD_HUB_PASSWORD', detectConfiguration.hubPassword)
        environmentVariables.put('SCAN_CLI_OPTS', dockerProperties.dockerProxyEnvironmentVariable())

        List<String> bashArguments = [
            "-c",
            "${shellScriptFile.absolutePath} --spring.config.location=\"${dockerPropertiesDirectory.getAbsolutePath()}\" ${imageArgument}"
        ]

        Executable dockerExecutable = new Executable(shellScriptFile.parentFile, environmentVariables, bashExecutablePath, bashArguments)
        executableRunner.execute(dockerExecutable)

        if (usingTarFile) {
            hubSignatureScanner.registerPathToScan(new File(detectConfiguration.dockerTar))
        }

        //At least for the moment, there is no way of running the hub-docker-inspector to generate the files only, so it currently handles all uploading
        []
    }
}
