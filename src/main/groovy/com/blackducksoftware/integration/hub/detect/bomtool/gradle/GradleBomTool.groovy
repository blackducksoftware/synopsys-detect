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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.hub.ScanPathSource
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

import groovy.transform.TypeChecked

@Component
@TypeChecked
class GradleBomTool extends BomTool<GradleApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(GradleBomTool.class)

    static final String BUILD_GRADLE_FILENAME = 'build.gradle'

    @Autowired
    HubSignatureScanner hubSignatureScanner

    @Autowired
    GradleReportParser gradleReportParser

    @Autowired
    GradleInspectorManager gradleInspectorManager

    @Override
    BomToolType getBomToolType() {
        return BomToolType.GRADLE
    }

    @Override
    GradleApplicableResult isBomToolApplicable(File directory) {
        File buildGradle = detectFileManager.findFile(directory, BUILD_GRADLE_FILENAME)

        if (buildGradle) {
            String gradleExe = findGradleExecutable(directory.toString())
            if (gradleExe) {
                return new GradleApplicableResult(directory, buildGradle, gradleExe);
            } else {
                logger.warn('Could not find a Gradle wrapper or executable')
            }
        }

        return null;
    }

    @Override
    BomToolExtractionResult extractDetectCodeLocations(GradleApplicableResult applicable) {
        List<DetectCodeLocation> codeLocations = extractCodeLocationsFromGradle(applicable)

        File[] additionalTargets = detectFileManager.findFilesToDepth(detectConfiguration.sourceDirectory, 'build', detectConfiguration.searchDepth)
        if (additionalTargets) {
            additionalTargets.each { File file ->
                hubSignatureScanner.registerPathToScan(ScanPathSource.GRADLE_SOURCE, file)
            }
        }
        bomToolExtractionResultsFactory.fromCodeLocations(codeLocations, getBomToolType(), applicable.directory)
    }

    private String findGradleExecutable(String sourcePath) {
        String gradlePath = executableManager.getExecutablePathOrOverride(ExecutableType.GRADLEW, false, sourcePath, detectConfiguration.getGradlePath())

        if (!gradlePath) {
            logger.debug('gradle wrapper not found - trying to find gradle on the PATH')
            gradlePath = executableManager.getExecutablePath(ExecutableType.GRADLE, true, sourcePath)
        }
        gradlePath
    }

    //#TODO: Bom tool finder - setting project name and version
    List<DetectCodeLocation> extractCodeLocationsFromGradle(GradleApplicableResult applicable) {
        String gradleCommand = detectConfiguration.gradleBuildCommand
        gradleCommand = gradleCommand?.replace('dependencies', '')?.trim()

        def arguments = []
        if (gradleCommand) {
            arguments.addAll(gradleCommand.split(' ') as List)
        }
        arguments.add("dependencies");
        arguments.add(String.format("--init-script=%s",gradleInspectorManager.getInitScriptPath()));

        logger.info("using ${gradleInspectorManager.getInitScriptPath()} as the path for the gradle init script")
        Executable executable = new Executable(applicable.directory, applicable.gradleExe, arguments)
        executableRunner.execute(executable)

        File buildDirectory = new File(applicable.directory, 'build')
        File blackduckDirectory = new File(buildDirectory, 'blackduck')

        File[] codeLocationFiles = detectFileManager.findFiles(blackduckDirectory, '*_dependencyGraph.txt')

        List<DetectCodeLocation> codeLocations = codeLocationFiles.collect { File file ->
            logger.debug("Parsing dependency graph : ${file.getName()}")
            gradleReportParser.parseDependencies(file.newInputStream()).codeLocation
        }
        if (detectConfiguration.getCleanupDetectFiles()) {
            blackduckDirectory.deleteDir()
        }
        codeLocations
    }

    String getInspectorVersion() {
        return gradleInspectorManager.getInspectorVersion()
    }
}
