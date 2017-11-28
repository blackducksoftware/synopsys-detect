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

import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetInspectorManager
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

    @Autowired
    NugetInspectorManager nugetInspectorManager

    private String nugetExecutable
    private File outputDirectory

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
        nugetInspectorManager.installInspector(nugetExecutable, outputDirectory)

        if (!nugetInspectorManager.getNugetInspectorExecutablePath()) {
            throw new Exception("Failed to find a suitable nuget inspector to run.")
        }

        List<String> options = [
            "--target_path=${sourcePath}" as String,
            "--output_directory=${outputDirectory.getCanonicalPath()}" as String,
            "--ignore_failure=${detectConfiguration.getNugetInspectorIgnoreFailure()}" as String
        ]
        if (detectConfiguration.getNugetInspectorExcludedModules()) {
            options.add("--excluded_modules=${detectConfiguration.getNugetInspectorExcludedModules()}" as String)
        }
        if (detectConfiguration.getNugetInspectorIncludedModules()) {
            options.add("--included_modules=${detectConfiguration.getNugetInspectorIncludedModules()}" as String)
        }
        if (detectConfiguration.getNugetPackagesRepoUrl()) {
            options.add("--packages_repo_url=${detectConfiguration.getNugetPackagesRepoUrl()}" as String)
        }
        if (logger.isTraceEnabled()) {
            options.add('-v')
        }

        def hubNugetInspectorExecutable = new Executable(sourceDirectory, nugetInspectorManager.getNugetInspectorExecutablePath(), options)
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

    String getInspectorVersion() {
        return nugetInspectorManager.getInspectorVersion(nugetExecutable)
    }
}
