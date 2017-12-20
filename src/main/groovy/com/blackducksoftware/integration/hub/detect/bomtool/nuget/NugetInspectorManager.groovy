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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NugetInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorManager.class)

    private String nugetInspectorExecutable
    private String inspectorVersion

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExecutableRunner executableRunner

    public String getNugetInspectorExecutablePath() {
        return nugetInspectorExecutable
    }

    public String getInspectorVersion(final String nugetExecutablePath) {
        if (!inspectorVersion) {
            if ('latest'.equalsIgnoreCase(detectConfiguration.getNugetInspectorPackageVersion())) {
                final def nugetOptions = [
                    'list',
                    detectConfiguration.getNugetInspectorPackageName()
                ]
                def airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath())
                if (airGapNugetInspectorDirectory.exists()) {
                    logger.debug('Running in airgap mode. Resolving version from local path')
                    nugetOptions.addAll([
                        '-Source',
                        detectConfiguration.getNugetInspectorAirGapPath()
                    ])
                } else {
                    logger.debug('Running online. Resolving version through nuget')
                    nugetOptions.addAll([
                        '-Source',
                        detectConfiguration.getNugetPackagesRepoUrl()
                    ])
                }
                Executable getInspectorVersionExecutable = new Executable(detectConfiguration.sourceDirectory, nugetExecutablePath, nugetOptions)

                List<String> output = executableRunner.execute(getInspectorVersionExecutable).standardOutputAsList
                for (String line : output) {
                    String[] lineChunks = line.split(' ')
                    if (detectConfiguration.getNugetInspectorPackageName()?.equalsIgnoreCase(lineChunks[0])) {
                        inspectorVersion = lineChunks[1]
                    }
                }
                logger.info("Resolved nuget inspector version from latest to: ${inspectorVersion}")
            } else {
                inspectorVersion = detectConfiguration.getNugetInspectorPackageVersion()
            }
        }
        return inspectorVersion
    }

    private void installInspector(final String nugetExecutablePath, final File outputDirectory) {
        File toolsDirectory
        String inspectorVersion = detectConfiguration.getNugetInspectorPackageVersion()

        def airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath())
        if (airGapNugetInspectorDirectory.exists()) {
            logger.debug('Running in airgap mode. Resolving from local path')
            toolsDirectory = new File(airGapNugetInspectorDirectory, 'tools')
        } else {
            logger.debug('Running online. Resolving through nuget')
            final def nugetOptions = [
                'install',
                detectConfiguration.getNugetInspectorPackageName(),
                '-OutputDirectory',
                outputDirectory.getCanonicalPath(),
                '-Source',
                detectConfiguration.getNugetPackagesRepoUrl()
            ]
            nugetOptions.addAll([
                '-Version',
                inspectorVersion
            ])
            Executable installInspectorExecutable = new Executable(detectConfiguration.sourceDirectory, nugetExecutablePath, nugetOptions)
            executableRunner.execute(installInspectorExecutable)

            final File inspectorVersionDirectory = new File(outputDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.${inspectorVersion}")
            toolsDirectory = new File(inspectorVersionDirectory, 'tools')
        }
        final File inspectorExe = new File(toolsDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.exe")

        if (!inspectorExe.exists()) {
            logger.warn("Could not find the ${detectConfiguration.getNugetInspectorPackageName()} version: ${inspectorVersion} even after an install attempt.")
            return null
        }

        nugetInspectorExecutable = inspectorExe.getCanonicalPath()
    }
}
