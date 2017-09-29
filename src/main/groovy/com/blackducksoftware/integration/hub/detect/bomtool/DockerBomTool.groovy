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
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection
import com.blackducksoftware.integration.log.Slf4jIntLogger
import com.google.gson.Gson

import groovy.transform.TypeChecked
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response

@Component
@TypeChecked
class DockerBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(DockerBomTool.class)

    static final URL LATEST_URL = new URL('https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector.sh')
    static final String FORGE_SEPARATOR = "/"

    final String tarFileNamePattern = '*.tar.gz'
    final String dependenciesFileNamePattern = '*_dependencies.json'

    @Autowired
    Gson gson

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
            dockerExecutablePath = findExecutablePath(ExecutableType.DOCKER, true, detectConfiguration.dockerPath)
            bashExecutablePath = findExecutablePath(ExecutableType.BASH, true, detectConfiguration.bashPath)
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
            UnauthenticatedRestConnection restConnection = new UnauthenticatedRestConnection(new Slf4jIntLogger(logger), hubDockerInspectorShellScriptUrl, detectConfiguration.getHubTimeout())
            restConnection.alwaysTrustServerCertificate = detectConfiguration.hubTrustCertificate
            HttpUrl httpUrl = restConnection.createHttpUrl()
            Request request = restConnection.createGetRequest(httpUrl)
            String shellScriptContents = null
            Response response = null
            try {
                response = restConnection.handleExecuteClientCall(request)
                shellScriptContents =  response.body().string()
            } finally {
                if (response != null) {
                    response.close()
                }
            }
            shellScriptFile = detectFileManager.createFile(getBomToolType(), "hub-docker-inspector-${detectConfiguration.dockerInspectorVersion}.sh")
            detectFileManager.writeToFile(shellScriptFile, shellScriptContents)
            shellScriptFile.setExecutable(true)
        }

        File dockerPropertiesFile = detectFileManager.createFile(getBomToolType(), 'application.properties')
        File dockerBomToolDirectory =  dockerPropertiesFile.getParentFile()
        dockerProperties.populatePropertiesFile(dockerPropertiesFile, dockerBomToolDirectory)


        boolean usingTarFile = false
        String imageArgument = ''
        if (detectConfiguration.dockerImage) {
            imageArgument = detectConfiguration.dockerImage
        } else {
            File dockerTarFile = new File(detectConfiguration.dockerTar)
            imageArgument = dockerTarFile.getCanonicalPath()
            usingTarFile = true
        }

        String path = System.getenv('PATH')
        File dockerExecutableFile = new File(dockerExecutablePath)
        path += File.pathSeparator + dockerExecutableFile.parentFile.absolutePath
        Map<String, String> environmentVariables = [PATH: path]

        List<String> bashArguments = [
            "-c",
            "${shellScriptFile.absolutePath} --spring.config.location=\"${dockerBomToolDirectory.getAbsolutePath()}\" --dry.run=true ${imageArgument}" as String
        ]
        Executable dockerExecutable = new Executable(dockerBomToolDirectory, environmentVariables, bashExecutablePath, bashArguments)
        executableRunner.execute(dockerExecutable)

        if (usingTarFile) {
            hubSignatureScanner.registerPathToScan(new File(detectConfiguration.dockerTar))
        } else {
            File producedTarFile = detectFileManager.findFile(dockerBomToolDirectory, tarFileNamePattern)
            if (producedTarFile) {
                hubSignatureScanner.registerPathToScan(producedTarFile)
            } else {
                logMissingFile(dockerBomToolDirectory, tarFileNamePattern)
            }
        }

        File dependencyNodeJsonFile = detectFileManager.findFile(dockerBomToolDirectory, dependenciesFileNamePattern)
        if (dependencyNodeJsonFile) {
            String codeLocationJson = dependencyNodeJsonFile.getText(StandardCharsets.UTF_8.toString())
            DetectCodeLocation codeLocation = gson.fromJson(codeLocationJson, DetectCodeLocation.class)
            return [codeLocation]
        } else {
            logMissingFile(dockerBomToolDirectory, dependenciesFileNamePattern)
        }

        []
    }

    private void logMissingFile(File searchDirectory, String fileNamePattern) {
        logger.debug("No files found matching pattern [${fileNamePattern}]. Expected docker-inspector to produce file in ${searchDirectory.getCanonicalPath()}")
    }
}
