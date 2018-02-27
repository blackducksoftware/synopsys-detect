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

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenCodeLocationPackager
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.hub.ScanPathSource
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

import groovy.transform.TypeChecked

@Component
@TypeChecked
class MavenBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(MavenBomTool.class)

    static final String POM_FILENAME = 'pom.xml'
    static final String POM_WRAPPER_FILENAME = 'pom.groovy'

    @Autowired
    MavenCodeLocationPackager mavenCodeLocationPackager

    @Autowired
    HubSignatureScanner hubSignatureScanner

    private String mvnExecutable

    BomToolType getBomToolType() {
        return BomToolType.MAVEN
    }

    boolean isBomToolApplicable() {
        String pomXmlPath = detectFileManager.findFile(sourcePath, POM_FILENAME)
        String pomWrapperPath = detectFileManager.findFile(sourcePath, POM_WRAPPER_FILENAME)

        if (pomXmlPath || pomWrapperPath) {
            mvnExecutable = findMavenExecutablePath()
            if (!mvnExecutable) {
                logger.warn('Could not find a Maven wrapper or executable')
            }
        }

        mvnExecutable && (pomXmlPath || pomWrapperPath)
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        String mavenCommand = detectConfiguration.mavenBuildCommand
        mavenCommand = mavenCommand?.replace('dependency:tree', '')?.trim()

        def arguments = []
        if (mavenCommand) {
            arguments.addAll(mavenCommand.split(' ') as List)
        }
        if (detectConfiguration.getMavenScope()?.trim()) {
            arguments.add("-Dscope=${detectConfiguration.getMavenScope()}")
        }
        arguments.add('dependency:tree')

        final Executable mvnExecutable = new Executable(detectConfiguration.sourceDirectory, mvnExecutable, arguments)
        final ExecutableOutput mvnOutput = executableRunner.execute(mvnExecutable)

        String excludedModules = detectConfiguration.getMavenExcludedModuleNames()
        String includedModules = detectConfiguration.getMavenIncludedModuleNames()
        List<DetectCodeLocation> codeLocations = mavenCodeLocationPackager.extractCodeLocations(sourcePath, mvnOutput.standardOutput, excludedModules, includedModules)

        File[] additionalTargets = detectFileManager.findFilesToDepth(detectConfiguration.sourceDirectory, 'target', detectConfiguration.searchDepth)
        if (additionalTargets) {
            additionalTargets.each { File target ->
                hubSignatureScanner.registerPathToScan(ScanPathSource.MAVEN_SOURCE, target)
            }
        }

        codeLocations
    }

    private String findMavenExecutablePath() {
        if (StringUtils.isNotBlank(detectConfiguration.getMavenPath())) {
            return detectConfiguration.getMavenPath()
        }

        String wrapperPath = executableManager.getExecutablePath(ExecutableType.MVNW, false, sourcePath)
        if (wrapperPath) {
            return wrapperPath
        }

        executableManager.getExecutablePath(ExecutableType.MVN, true, sourcePath)
    }
}