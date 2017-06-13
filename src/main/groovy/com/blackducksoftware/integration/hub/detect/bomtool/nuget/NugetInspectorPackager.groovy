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

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.DetectProperties
import com.blackducksoftware.integration.hub.detect.util.FileFinder
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
class NugetInspectorPackager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorPackager.class)

    @Autowired
    DetectProperties detectProperties

    @Autowired
    FileFinder fileFinder

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    NugetNodeTransformer nugetNodeTransformer

    DependencyNode makeDependencyNode(String sourcePath, File nugetExecutable) {
        def outputDirectory = new File(new File(detectProperties.getOutputDirectoryPath()), 'nuget')
        def sourceDirectory = new File(sourcePath)
        String inspectorExePath = getInspectorExePath(sourceDirectory, outputDirectory, nugetExecutable)

        if (!inspectorExePath) {
            return null
        }

        def options =  [
            "--target_path=${sourcePath}",
            "--output_directory=${outputDirectory.getAbsolutePath()}",
            "--ignore_failure=${detectProperties.getNugetInspectorIgnoreFailure()}"
        ]
        if (detectProperties.getNugetInspectorExcludedModules()) {
            options += "--excluded_modules=${detectProperties.getNugetInspectorExcludedModules()}"
        }
        if (logger.traceEnabled) {
            options += "-v"
        }

        def hubNugetInspectorExecutable = new Executable(sourceDirectory, inspectorExePath, options)
        ExecutableOutput executableOutput = executableRunner.executeLoudly(hubNugetInspectorExecutable)

        def dependencyNodeFile = fileFinder.findFile(outputDirectory, '*_dependency_node.json')
        DependencyNode node = nugetNodeTransformer.parse(dependencyNodeFile)
        FileUtils.deleteDirectory(outputDirectory)
        return node
    }

    private String getInspectorExePath(File sourceDirectory, File outputDirectory, File nugetExecutable) {
        File inspectorVersionDirectory = new File(outputDirectory, "${detectProperties.getNugetInspectorPackageName()}.${detectProperties.getNugetInspectorPackageVersion()}")
        File toolsDirectory = new File(inspectorVersionDirectory, 'tools')
        File inspectorExe = new File(toolsDirectory, "${detectProperties.getNugetInspectorPackageName()}.exe")

        //if we can't find the inspector where we expect to, attempt to install it from nuget.org
        if (inspectorExe == null || !inspectorExe.exists()) {
            installInspectorFromNugetDotOrg(sourceDirectory, outputDirectory, nugetExecutable)
            inspectorExe = new File(toolsDirectory, "${detectProperties.getNugetInspectorPackageName()}.exe")
        }

        if (inspectorExe == null || !inspectorExe.exists()) {
            logger.error("Could not find the ${detectProperties.getNugetInspectorPackageName()} version:${detectProperties.getNugetInspectorPackageVersion()} even after an install attempt.")
            return null
        }

        return inspectorExe.absolutePath
    }

    private ExecutableOutput installInspectorFromNugetDotOrg(File sourceDirectory, File outputDirectory, File nugetExecutable) {
        def options =  [
            'install',
            detectProperties.getNugetInspectorPackageName(),
            '-Version',
            detectProperties.getNugetInspectorPackageVersion(),
            '-OutputDirectory',
            outputDirectory.absolutePath
        ]

        Executable installExecutable = new Executable(sourceDirectory, nugetExecutable.absolutePath, options)
        executableRunner.executeLoudly(installExecutable)
    }
}