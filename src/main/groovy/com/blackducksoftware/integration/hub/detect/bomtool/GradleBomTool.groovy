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
import com.blackducksoftware.integration.hub.detect.Application
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.google.gson.Gson

@Component
class GradleBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GradleBomTool.class)

    static final String BUILD_GRADLE_FILENAME = 'build.gradle'

    @Autowired
    Gson gson

    @Autowired
    HubSignatureScanner hubSignatureScanner

    private String gradleExecutable

    BomToolType getBomToolType() {
        return BomToolType.GRADLE
    }

    boolean isBomToolApplicable() {
        def buildGradle = detectFileManager.findFile(sourcePath, BUILD_GRADLE_FILENAME)

        if (buildGradle) {
            gradleExecutable = findGradleExecutable(sourcePath)
            if (!gradleExecutable) {
                logger.warn('Could not find a Gradle wrapper or executable')
            }
        }

        buildGradle && gradleExecutable
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        List<DetectCodeLocation> codeLocations = new ArrayList<>()
        List<DependencyNode> projectNodes = extractProjectNodes()
        projectNodes.each {
            // Set the source path of the DetectCodeLocation to the name of the node since we dont know the path of the project it came from
            DetectCodeLocation detectCodeLocation = new DetectCodeLocation(getBomToolType(), it.name, it.name, it.version, null, it.externalId, it.children)
            codeLocations.add(detectCodeLocation)
        }
        File[] additionalTargets = detectFileManager.findFilesToDepth(detectConfiguration.sourceDirectory, 'build', detectConfiguration.searchDepth)
        if (additionalTargets) {
            additionalTargets.each { hubSignatureScanner.registerPathToScan(it) }
        }
        codeLocations
    }

    private String findGradleExecutable(String sourcePath) {
        String gradlePath = detectConfiguration.getGradlePath()
        if (StringUtils.isBlank(gradlePath)) {
            logger.debug('detect.gradle.path not set in config - first try to find the gradle wrapper')
            gradlePath = executableManager.getPathOfExecutableFromRelativePath(sourcePath, ExecutableType.GRADLEW)
        }

        if (StringUtils.isBlank(gradlePath)) {
            logger.debug('gradle wrapper not found - trying to find gradle on the PATH')
            gradlePath = executableManager.getPathOfExecutable(ExecutableType.GRADLE)
        }
        gradlePath
    }

    List<DependencyNode> extractProjectNodes() {
        File initScriptFile = detectFileManager.createFile(BomToolType.GRADLE, 'init-detect.gradle')
        String initScriptContents = getClass().getResourceAsStream('/init-script-gradle').getText(StandardCharsets.UTF_8.name())
        initScriptContents = initScriptContents.replace('GRADLE_INSPECTOR_VERSION', detectConfiguration.getGradleInspectorVersion())
        initScriptContents = initScriptContents.replace('EXCLUDED_PROJECT_NAMES', detectConfiguration.getGradleExcludedProjectNames())
        initScriptContents = initScriptContents.replace('INCLUDED_PROJECT_NAMES', detectConfiguration.getGradleIncludedProjectNames())
        initScriptContents = initScriptContents.replace('EXCLUDED_CONFIGURATION_NAMES', detectConfiguration.getGradleExcludedConfigurationNames())
        initScriptContents = initScriptContents.replace('INCLUDED_CONFIGURATION_NAMES', detectConfiguration.getGradleIncludedConfigurationNames())

        detectFileManager.writeToFile(initScriptFile, initScriptContents)
        String initScriptPath = initScriptFile.absolutePath
        logger.info("using ${initScriptPath} as the path for the gradle init script")
        Executable executable = new Executable(sourceDirectory, gradleExecutable, [
            detectConfiguration.getGradleBuildCommand(),
            "--init-script=${initScriptPath}"
        ])
        executableRunner.execute(executable)

        File buildDirectory = new File(sourcePath, 'build')
        File blackduckDirectory = new File(buildDirectory, 'blackduck')

        List<DependencyNode> nodes = new ArrayList<>()

        File[] dependencyNodeFiles = detectFileManager.findFiles(blackduckDirectory, '*_dependencyNodes.json')
        dependencyNodeFiles.each {
            logger.debug("Dependency Node file name: ${it.getName()}")
            String dependencyNodeJson = it.getText(StandardCharsets.UTF_8.name())
            DependencyNode projectDependencyNode = gson.fromJson(dependencyNodeJson, DependencyNode.class)
            nodes.add(projectDependencyNode)
        }
        extractProjectInformation(blackduckDirectory)

        if (detectConfiguration.gradleCleanupBuildBlackduckDirectory) {
            blackduckDirectory.deleteDir()
        }
        nodes
    }

    private void extractProjectInformation(File blackduckDirectory){
        File projectInfoFile = new File(blackduckDirectory, 'ProjectInfo.txt')
        String[] projectInfoLines = projectInfoFile.text.split(Application.LINE_SEPARATOR)
        projectName = projectInfoLines[0]
        projectVersion = projectInfoLines[1]
    }
}