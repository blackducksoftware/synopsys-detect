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
package com.blackducksoftware.integration.hub.detect.bomtool

import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.BdioReader
import com.blackducksoftware.integration.hub.bdio.BdioTransformer
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerInspectorManager
import com.blackducksoftware.integration.hub.detect.bomtool.docker.DockerProperties
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.google.gson.Gson

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DockerBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(DockerBomTool.class)

    final String tarFilenamePattern = '*.tar.gz'
    final String dependenciesFilenamePattern = '*bdio.jsonld'

    @Autowired
    Gson gson

    @Autowired
    DockerProperties dockerProperties

    @Autowired
    HubSignatureScanner hubSignatureScanner

    @Autowired
    ExternalIdFactory externalIdFactory

    @Autowired
    BdioTransformer bdioTransformer

    @Autowired
    DockerInspectorManager dockerInspectorManager

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

    @Override
    List<DetectCodeLocation> extractDetectCodeLocations() {
        File dockerPropertiesFile = detectFileManager.createFile(getBomToolType(), 'application.properties')
        File dockerBomToolDirectory =  dockerPropertiesFile.getParentFile()
        dockerProperties.populatePropertiesFile(dockerPropertiesFile, dockerBomToolDirectory)

        File dockerInspectorShellScript = dockerInspectorManager.getShellScript()

        boolean usingTarFile = false
        String imageArgument = ''
        if (detectConfiguration.dockerImage) {
            imageArgument = String.format("--docker.image=%s", detectConfiguration.dockerImage)
        } else {
            File dockerTarFile = new File(detectConfiguration.dockerTar)
            imageArgument = String.format("--docker.tar=%s", dockerTarFile.getCanonicalPath())
            usingTarFile = true
        }

        String path = System.getenv('PATH')
        File dockerExecutableFile = new File(dockerExecutablePath)
        path += File.pathSeparator + dockerExecutableFile.parentFile.getCanonicalPath()
        Map<String, String> environmentVariables = [PATH: path]
        if (!'latest'.equals(detectConfiguration.dockerInspectorVersion)) {
            environmentVariables.put('DOCKER_INSPECTOR_VERSION', detectConfiguration.dockerInspectorVersion)
        }

        List<String> bashArguments = [
            '-c',
            "\"${dockerInspectorShellScript.getCanonicalPath()}\" --spring.config.location=\"file:${dockerPropertiesFile.getCanonicalPath()}\" --dry.run=true --no.prompt=true ${imageArgument}" as String
        ]
        def airGapHubDockerInspectorJar = new File("${detectConfiguration.getDockerInspectorAirGapPath()}", "hub-docker-inspector-${dockerInspectorManager.getInspectorVersion(bashExecutablePath)}.jar")
        if (airGapHubDockerInspectorJar.exists()) {
            try {
                for (String os : ['ubuntu', 'alpine', 'centos']) {
                    def dockerImage = new File(airGapHubDockerInspectorJar.getParentFile(), "hub-docker-inspector-${os}.tar")
                    List<String> dockerImportArguments = [
                        '-c',
                        "docker load -i \"${dockerImage.getCanonicalPath()}\"" as String
                    ]
                    bashArguments[1] = "\"${dockerInspectorShellScript.getCanonicalPath()}\" --spring.config.location=\"file:${dockerPropertiesFile.getCanonicalPath()}\" --dry.run=true --no.prompt=true --jar.path=\"${airGapHubDockerInspectorJar.getCanonicalPath()}\" ${imageArgument}" as String
                    Executable dockerImportImageExecutable = new Executable(dockerBomToolDirectory, environmentVariables, bashExecutablePath, dockerImportArguments)
                    executableRunner.execute(dockerImportImageExecutable)
                }
            } catch (Exception e) {
                logger.debug('Exception encountered when resolving paths for docker air gap, running in online mode instead')
                logger.debug(e.getMessage())
            }
        }
        Executable dockerExecutable = new Executable(dockerBomToolDirectory, environmentVariables, bashExecutablePath, bashArguments)
        executableRunner.execute(dockerExecutable)

        if (usingTarFile) {
            hubSignatureScanner.registerPathToScan(new File(detectConfiguration.dockerTar))
        } else {
            File producedTarFile = detectFileManager.findFile(dockerBomToolDirectory, tarFilenamePattern)
            if (producedTarFile) {
                hubSignatureScanner.registerPathToScan(producedTarFile)
            } else {
                logMissingFile(dockerBomToolDirectory, tarFilenamePattern)
            }
        }

        File bdioFile = detectFileManager.findFile(dockerBomToolDirectory, dependenciesFilenamePattern)
        if (bdioFile) {
            SimpleBdioDocument simpleBdioDocument = null
            BdioReader bdioReader = null
            try {
                final InputStream dockerOutputInputStream = new FileInputStream(bdioFile)
                bdioReader = new BdioReader(gson, dockerOutputInputStream)
                simpleBdioDocument = bdioReader.readSimpleBdioDocument()
            } finally {
                IOUtils.closeQuietly(bdioReader)
            }

            final DependencyGraph dependencyGraph = bdioTransformer.transformToDependencyGraph(simpleBdioDocument.project, simpleBdioDocument.components)

            String projectName = simpleBdioDocument.project.name
            String projectVersionName = simpleBdioDocument.project.version

            Forge dockerForge = new Forge(ExternalId.BDIO_ID_SEPARATOR, ExternalId.BDIO_ID_SEPARATOR, simpleBdioDocument.project.bdioExternalIdentifier.forge)
            String externalIdPath = simpleBdioDocument.project.bdioExternalIdentifier.externalId
            ExternalId projectExternalId = externalIdFactory.createPathExternalId(dockerForge, externalIdPath)

            DetectCodeLocation detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, projectName, projectVersionName, projectExternalId, dependencyGraph)
            return [detectCodeLocation]
        } else {
            logMissingFile(dockerBomToolDirectory, dependenciesFilenamePattern)
        }

        []
    }

    String getInspectorVersion() {
        return dockerInspectorManager.getInspectorVersion(bashExecutablePath)
    }

    private void logMissingFile(File searchDirectory, String filenamePattern) {
        logger.debug("No files found matching pattern [${filenamePattern}]. Expected docker-inspector to produce file in ${searchDirectory.getCanonicalPath()}")
    }
}
