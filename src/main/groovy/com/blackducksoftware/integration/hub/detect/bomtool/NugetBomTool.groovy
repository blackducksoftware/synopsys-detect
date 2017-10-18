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

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorPackager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NugetBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(NugetBomTool.class)

    static final String SOLUTION_PATTERN = '*.sln'
    static final String PROJECT_PATTERN = '*.*proj'
    static final String INSPECTOR_OUTPUT_PATTERN ='*_inspection.json'

    @Autowired
    NugetInspectorPackager nugetInspectorPackager

    private String nugetExecutable
    private String nugetInspectorExecutable
    private File outputDirectory
    private String inspectorVersion

    BomToolType getBomToolType() {
        return BomToolType.NUGET
    }

    @Override
    public boolean isBomToolApplicable() {
        def containsSolutionFile = detectFileManager.containsAllFiles(sourcePath, SOLUTION_PATTERN)
        def containsProjectFile = detectFileManager.containsAllFiles(sourcePath, PROJECT_PATTERN)

        if (containsSolutionFile || containsProjectFile) {
            nugetExecutable = findExecutablePath(ExecutableType.NUGET, true, detectConfiguration.getNugetPath())
            if (!nugetExecutable) {
                logger.warn("Could not find a ${executableManager.getExecutableName(ExecutableType.NUGET)} executable")
            }
            outputDirectory = new File(detectConfiguration.outputDirectory, 'nuget')
        }

        nugetExecutable && (containsSolutionFile || containsProjectFile)
    }

    @Override
    List<DetectCodeLocation> extractDetectCodeLocations() {
        if (!nugetInspectorExecutable) {
            return []
        }

        List<String> options =  [
            "--target_path=${sourcePath}" as String,
            "--output_directory=${outputDirectory.getCanonicalPath()}" as String,
            "--ignore_failure=${detectConfiguration.getNugetInspectorIgnoreFailure()}" as String
        ]
        if (detectConfiguration.getNugetInspectorExcludedModules()) {
            options.add("--excluded_modules=${detectConfiguration.getNugetInspectorExcludedModules()}" as String)
        }
        if (detectConfiguration.getNugetPackagesRepoUrl()) {
            options.add("--packages_repo_url=${detectConfiguration.getNugetPackagesRepoUrl()}" as String)
        }
        if (logger.traceEnabled) {
            options.add('-v')
        }

        def hubNugetInspectorExecutable = new Executable(sourceDirectory, nugetInspectorExecutable, options)
        ExecutableOutput executableOutput = executableRunner.execute(hubNugetInspectorExecutable)

        def dependencyNodeFiles = detectFileManager.findFiles(outputDirectory, INSPECTOR_OUTPUT_PATTERN)
        List<DetectCodeLocation> codeLocations = dependencyNodeFiles?.collectMany { nugetInspectorPackager.createDetectCodeLocation(it) }
        if (detectConfiguration.cleanupBomToolFiles) {
            try {
                FileUtils.deleteDirectory(outputDirectory)
            } catch (Exception e){
                logger.warn("Unable to clean up nuget files: ${outputDirectory}")
            }
        }

        if (!codeLocations) {
            logger.warn('Unable to extract any dependencies from nuget')
            return []
        }

        codeLocations
    }

    public String getInspectorVersion() {
        if ('latest'.equalsIgnoreCase(detectConfiguration.getNugetInspectorPackageVersion())) {
            if (!inspectorVersion) {
                final def nugetOptions = [
                    'list',
                    detectConfiguration.getNugetInspectorPackageName()
                ]
                def airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath())
                if (airGapNugetInspectorDirectory.exists()) {
                    logger.debug("Running in airgap mode. Resolving version from local path")
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
                Executable getInspectorVersionExecutable = new Executable(detectConfiguration.sourceDirectory, nugetExecutable, nugetOptions)

                List<String> output = executableRunner.execute(getInspectorVersionExecutable).standardOutputAsList
                for (String line : output) {
                    String[] lineChunks = line.split(" ")
                    if (detectConfiguration.getNugetInspectorPackageName()?.equalsIgnoreCase(lineChunks[0])) {
                        return lineChunks[1]
                    }
                }
            }
        } else {
            inspectorVersion = detectConfiguration.getDockerInspectorVersion()
        }
        return inspectorVersion
    }

    private String installInspector() {
        final File inspectorVersionDirectory = new File(outputDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.${detectConfiguration.getNugetInspectorPackageVersion()}")
        final File toolsDirectory = new File(inspectorVersionDirectory, 'tools')
        final File inspectorExe = new File(toolsDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.exe")

        final def nugetOptions = [
            'install',
            detectConfiguration.getNugetInspectorPackageName(),
            '-OutputDirectory',
            outputDirectory.getCanonicalPath()
        ]

        def airGapNugetInspectorDirectory = new File(detectConfiguration.getNugetInspectorAirGapPath())
        if (airGapNugetInspectorDirectory.exists()) {
            logger.debug("Running in airgap mode. Resolving from local path")
            nugetOptions.addAll([
                '-Source',
                detectConfiguration.getNugetInspectorAirGapPath()
            ])
        } else {
            logger.debug('Running online. Resolving through nuget')
            if (!'latest'.equalsIgnoreCase(detectConfiguration.getNugetInspectorPackageVersion())) {
                nugetOptions.addAll([
                    '-Version',
                    detectConfiguration.getNugetInspectorPackageVersion()
                ])
            }
            nugetOptions.addAll([
                '-Source',
                detectConfiguration.getNugetPackagesRepoUrl()
            ])
        }


        if (!inspectorExe.exists()) {
            Executable installInspectorExecutable = new Executable(detectConfiguration.sourceDirectory, nugetExecutable, nugetOptions)
            executableRunner.execute(installInspectorExecutable)
        } else {
            logger.info("Existing nuget inspector found at ${inspectorExe.getCanonicalPath()}")
        }

        if (!inspectorExe.exists()) {
            logger.warn("Could not find the ${detectConfiguration.getNugetInspectorPackageName()} version:${detectConfiguration.getNugetInspectorPackageVersion()} even after an install attempt.")
            return null
        }

        inspectorExe.getCanonicalPath()
    }
}