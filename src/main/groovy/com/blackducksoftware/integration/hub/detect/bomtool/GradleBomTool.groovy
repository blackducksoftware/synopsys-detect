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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleDependenciesParser
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorManager
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

import groovy.transform.TypeChecked

@Component
@TypeChecked
class GradleBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GradleBomTool.class)

    static final String BUILD_GRADLE_FILENAME = 'build.gradle'

    @Autowired
    HubSignatureScanner hubSignatureScanner

    @Autowired
    GradleDependenciesParser gradleDependenciesParser

    @Autowired
    GradleInspectorManager gradleInspectorManager

    private String gradleExecutable

    @Override
    BomToolType getBomToolType() {
        return BomToolType.GRADLE
    }

    @Override
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


    @Override
    List<DetectCodeLocation> extractDetectCodeLocations(DetectProject detectProject) {
        List<DetectCodeLocation> codeLocations = extractCodeLocationsFromGradle(detectProject)

        File[] additionalTargets = detectFileManager.findFilesToDepth(detectConfiguration.sourceDirectory, 'build', detectConfiguration.searchDepth)
        if (additionalTargets) {
            additionalTargets.each { File file ->
                hubSignatureScanner.registerPathToScan(file)
            }
        }
        codeLocations
    }

    private String findGradleExecutable(String sourcePath) {
        String gradlePath = findExecutablePath(ExecutableType.GRADLEW, false, detectConfiguration.getGradlePath())

        if (!gradlePath) {
            logger.debug('gradle wrapper not found - trying to find gradle on the PATH')
            gradlePath = executableManager.getExecutablePath(ExecutableType.GRADLE, true, sourcePath)
        }
        gradlePath
    }

    List<DetectCodeLocation> extractCodeLocationsFromGradle(DetectProject detectProject) {
        logger.info("using ${gradleInspectorManager.getInitScriptPath()} as the path for the gradle init script")
        Executable executable = new Executable(sourceDirectory, gradleExecutable, [
            detectConfiguration.getGradleBuildCommand(),
            "--init-script=${gradleInspectorManager.getInitScriptPath()}" as String
        ])
        executableRunner.execute(executable)

        File buildDirectory = new File(sourcePath, 'build')
        File blackduckDirectory = new File(buildDirectory, 'blackduck')

        File[] codeLocationFiles = detectFileManager.findFiles(blackduckDirectory, '*_dependencyGraph.txt')

        List<DetectCodeLocation> codeLocations = codeLocationFiles.collect { File file ->
            logger.debug("Parsing dependency graph : ${file.getName()}")
            gradleDependenciesParser.parseDependencies(detectProject, file.newInputStream())
        }
        if (detectConfiguration.gradleCleanupBuildBlackduckDirectory) {
            blackduckDirectory.deleteDir()
        }
        codeLocations
    }

    String getInspectorVersion() {
        return gradleInspectorManager.getInspectorVersion()
    }
}