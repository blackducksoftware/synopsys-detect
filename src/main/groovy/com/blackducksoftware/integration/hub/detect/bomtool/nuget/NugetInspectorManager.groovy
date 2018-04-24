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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolInspectorManager
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NugetInspectorManager extends BomToolInspectorManager{
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorManager.class)

    private String nugetInspectorExecutable
    private String inspectorVersion

    public void install() {
        def nugetExecutable = executableManager.getExecutablePathOrOverride(ExecutableType.NUGET, true, detectConfiguration.getSourceDirectory(), detectConfiguration.getNugetPath())
        inspectorVersion = resolveInspectorVersion(nugetExecutable);
        if (inspectorVersion) {
            nugetInspectorExecutable = installInspector(nugetExecutable, new File(detectConfiguration.outputDirectory, 'nuget'), inspectorVersion)
            if (!nugetInspectorExecutable) {
                throw new DetectUserFriendlyException("Unable to install nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
            }
        } else {
            throw new DetectUserFriendlyException("Unable to resolve nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    public BomToolType getBomToolType() {
        return BomToolType.NUGET;
    }

    public String getNugetInspectorExecutablePath() {
        return nugetInspectorExecutable;
    }

    public String getInspectorVersion() {
        return inspectorVersion;
    }

    private String resolveInspectorVersion(final String nugetExecutablePath) {
        if ('latest'.equalsIgnoreCase(detectConfiguration.getNugetInspectorPackageVersion())) {
            if (shouldUseAirGap()) {
                logger.debug('Running in airgap mode. Resolving version from local path')
                return resolveVersionFromSource(detectConfiguration.getNugetInspectorAirGapPath(), nugetExecutablePath);
            } else {
                logger.debug('Running online. Resolving version through nuget')
                for (String source : detectConfiguration.getNugetPackagesRepoUrl()) {
                    logger.debug('Attempting source: ' + source);
                    def inspectorVersion = resolveVersionFromSource(source, nugetExecutablePath);
                    if (inspectorVersion) return inspectorVersion;
                }
            }
        } else {
            return detectConfiguration.getNugetInspectorPackageVersion()
        }
    }

    private boolean shouldUseAirGap() {
        def airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath())
        return airGapNugetInspectorDirectory.exists()
    }

    private String resolveVersionFromSource(String source, final String nugetExecutablePath) {
        String version = null;

        final def nugetOptions = [
            'list',
            detectConfiguration.getNugetInspectorPackageName(),
            '-Source',
            source
        ];

        Executable getInspectorVersionExecutable = new Executable(detectConfiguration.sourceDirectory, nugetExecutablePath, nugetOptions)

        List<String> output = executableRunner.execute(getInspectorVersionExecutable).standardOutputAsList
        for (String line : output) {
            String[] lineChunks = line.split(' ')
            if (detectConfiguration.getNugetInspectorPackageName()?.equalsIgnoreCase(lineChunks[0])) {
                version = lineChunks[1]
            }
        }

        return version;

    }

    private String installInspector(final String nugetExecutablePath, final File outputDirectory, String inspectorVersion) {
        File toolsDirectory

        def airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath())
        if (airGapNugetInspectorDirectory.exists()) {
            logger.debug('Running in airgap mode. Resolving from local path')
            toolsDirectory = new File(airGapNugetInspectorDirectory, 'tools')
        } else {
            logger.debug('Running online. Resolving through nuget')

            for (String source : detectConfiguration.getNugetPackagesRepoUrl()) {
                logger.debug('Attempting source: ' + source);
                def success = attemptInstallInspectorFromSource(source, nugetExecutablePath, outputDirectory);
                if (success) break;
            }

            final File inspectorVersionDirectory = new File(outputDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.${inspectorVersion}")
            toolsDirectory = new File(inspectorVersionDirectory, 'tools')
        }
        final File inspectorExe = new File(toolsDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.exe")

        if (!inspectorExe.exists()) {
            logger.warn("Could not find the ${detectConfiguration.getNugetInspectorPackageName()} version: ${inspectorVersion} even after an install attempt.")
            return null
        }

        return inspectorExe.getCanonicalPath()
    }

    private boolean attemptInstallInspectorFromSource(String source, final String nugetExecutablePath, final File outputDirectory) {
        final def nugetOptions = [
            'install',
            detectConfiguration.getNugetInspectorPackageName(),
            '-OutputDirectory',
            outputDirectory.getCanonicalPath(),
            '-Source',
            source,
            '-Version',
            inspectorVersion
        ]

        Executable installInspectorExecutable = new Executable(detectConfiguration.getSourceDirectory(), nugetExecutablePath, nugetOptions)
        def result = executableRunner.execute(installInspectorExecutable)

        if (result.returnCode == 0 && result.getErrorOutputAsList().size() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
