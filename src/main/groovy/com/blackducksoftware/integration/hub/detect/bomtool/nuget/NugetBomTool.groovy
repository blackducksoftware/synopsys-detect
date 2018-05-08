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

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphCombiner
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.detect.DetectInfo
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NugetBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(NugetBomTool.class)

    static final String SOLUTION_PATTERN = '*.sln'
    static final String INSPECTOR_OUTPUT_PATTERN ='*_inspection.json'

    //populated from "open project" in visual studio 2017
    static final String[] SUPPORTED_PROJECT_PATTERNS = [
        //C#
        "*.csproj",
        //F#
        "*.fsproj",
        //VB
        "*.vbproj",
        //Azure Stream Analytics
        "*.asaproj",
        //Docker Compose
        "*.dcproj",
        //Shared Projects
        "*.shproj",
        //Cloud Computing
        "*.ccproj",
        //Fabric Application
        "*.sfproj",
        //Node.js
        "*.njsproj",
        //VC++
        "*.vcxproj",
        //VC++
        "*.vcproj",
        //.NET Core
        "*.xproj",
        //Python
        "*.pyproj",
        //Hive
        "*.hiveproj",
        //Pig
        "*.pigproj",
        //JavaScript
        "*.jsproj",
        //U-SQL
        "*.usqlproj",
        //Deployment
        "*.deployproj",
        //Common Project System Files
        "*.msbuildproj",
        //SQL
        "*.sqlproj",
        //SQL Project Files
        "*.dbproj",
        //RStudio
        "*.rproj"
    ];

    @Autowired
    NugetInspectorPackager nugetInspectorPackager

    @Autowired
    NugetInspectorManager nugetInspectorManager

    @Autowired
    DetectInfo detectInfo

    private String nugetExecutable
    private File outputDirectory

    BomToolType getBomToolType() {
        return BomToolType.NUGET
    }

    @Override
    public boolean isBomToolApplicable() {
        if (OperatingSystemType.WINDOWS != detectInfo.getCurrentOs()) {
            logger.debug("Nuget can not apply to this OS. It can only apply to windows.");
            return false;
        }

        def containsSolutionFile = detectFileManager.containsAllFiles(sourcePath, SOLUTION_PATTERN)
        def containsProjectFile = SUPPORTED_PROJECT_PATTERNS.any { String pattern ->
            detectFileManager.containsAllFiles(sourcePath, pattern)
        }

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
            options.add("--packages_repo_url=${detectConfiguration.getNugetPackagesRepoUrl().join(',')}" as String)
        }
        if (logger.isTraceEnabled()) {
            options.add('-v')
        }

        def hubNugetInspectorExecutable = new Executable(sourceDirectory, nugetInspectorManager.getNugetInspectorExecutablePath(), options)
        ExecutableOutput executableOutput = executableRunner.execute(hubNugetInspectorExecutable)

        def dependencyNodeFiles = detectFileManager.findFiles(outputDirectory, INSPECTOR_OUTPUT_PATTERN)
        List<DetectCodeLocation> codeLocations = dependencyNodeFiles?.collectMany { nugetInspectorPackager.createDetectCodeLocation(it) }
        if (detectConfiguration.getCleanupDetectFiles()) {
            try {
                FileUtils.deleteDirectory(outputDirectory)
            } catch (Exception e) {
                logger.warn("Unable to clean up nuget files: ${outputDirectory}")
            }
        }

        if (!codeLocations) {
            logger.warn('Unable to extract any dependencies from nuget')
            return []
        }

        Map<String, DetectCodeLocation> codeLocationsBySource = new HashMap<>();
        DependencyGraphCombiner combiner = new DependencyGraphCombiner();

        codeLocations.forEach { DetectCodeLocation codeLocation ->
			String sourcePathKey = codeLocation.getSourcePath().toLowerCase();
            if (codeLocationsBySource.containsKey(sourcePathKey)) {
                logger.info("Multiple project code locations were generated for: " + codeLocation.sourcePath);
                logger.info("This most likely means the same project exists in multiple solutions.")
                logger.info("The code location's dependencies will be combined, in the future they will exist seperately for each solution.")
                DetectCodeLocation destination = codeLocationsBySource.get(sourcePathKey);
                combiner.addGraphAsChildrenToRoot((MutableDependencyGraph) destination.getDependencyGraph(), codeLocation.getDependencyGraph());
            } else {
                codeLocationsBySource.put(sourcePathKey, codeLocation);
            }
        }

        codeLocationsBySource.values().toList()
    }

    String getInspectorVersion() {
        return nugetInspectorManager.getInspectorVersion(nugetExecutable)
    }
}
