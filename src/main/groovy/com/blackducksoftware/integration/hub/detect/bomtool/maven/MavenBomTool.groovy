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
package com.blackducksoftware.integration.hub.detect.bomtool.maven

import org.apache.commons.lang3.StringUtils
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
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

import groovy.transform.TypeChecked

@Component
@TypeChecked
class MavenBomTool extends BomTool<MavenApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(MavenBomTool.class)

    static final String POM_FILENAME = 'pom.xml'
    static final String POM_WRAPPER_FILENAME = 'pom.groovy'

    @Autowired
    MavenCodeLocationPackager mavenCodeLocationPackager

    @Autowired
    HubSignatureScanner hubSignatureScanner

    BomToolType getBomToolType() {
        return BomToolType.MAVEN
    }

    MavenApplicableResult isBomToolApplicable(File directory) {
        File pomXmlPath = detectFileManager.findFile(directory, POM_FILENAME)
        File pomWrapperPath = detectFileManager.findFile(directory, POM_WRAPPER_FILENAME)

        if (pomXmlPath || pomWrapperPath) {
            def mvnExecutable = findMavenExecutablePath(directory)
            if (mvnExecutable) {
                return new MavenApplicableResult(directory, pomXmlPath, pomWrapperPath, mvnExecutable)
            } else {
                logger.warn('Could not find the Maven executable mvn, please ensure that Maven has been installed correctly.')
            }
        }

        return null;
    }

    BomToolExtractionResult extractDetectCodeLocations(MavenApplicableResult applicable) {
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

        final Executable mvnExecutable = new Executable(applicable.directory, applicable.mavenExe, arguments)
        final ExecutableOutput mvnOutput = executableRunner.execute(mvnExecutable)

        String excludedModules = detectConfiguration.getMavenExcludedModuleNames()
        String includedModules = detectConfiguration.getMavenIncludedModuleNames()
        List<DetectCodeLocation> codeLocations = mavenCodeLocationPackager.extractCodeLocations(applicable.directory.toString(), mvnOutput.standardOutput, excludedModules, includedModules)

        File[] additionalTargets = detectFileManager.findFilesToDepth(applicable.directory, 'target', detectConfiguration.searchDepth)
        if (additionalTargets) {
            additionalTargets.each { File target ->
                hubSignatureScanner.registerPathToScan(ScanPathSource.MAVEN_SOURCE, target)
            }
        }

        bomToolExtractionResultsFactory.fromCodeLocations(codeLocations, getBomToolType(), applicable.directory)
    }

    private String findMavenExecutablePath(File directory) {
        if (StringUtils.isNotBlank(detectConfiguration.getMavenPath())) {
            return detectConfiguration.getMavenPath()
        }

        String wrapperPath = executableManager.getExecutablePath(ExecutableType.MVNW, false, directory.toString())
        if (wrapperPath) {
            return wrapperPath
        }

        executableManager.getExecutablePath(ExecutableType.MVN, true, directory.toString())
    }
}
