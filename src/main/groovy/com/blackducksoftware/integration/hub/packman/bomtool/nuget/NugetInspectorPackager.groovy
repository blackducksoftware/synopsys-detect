/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.bomtool.nuget

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.executable.Executable
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunner

@Component
class NugetInspectorPackager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorPackager.class)

    @Autowired
    PackmanProperties packmanProperties

    @Autowired
    FileFinder fileFinder

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    NugetNodeTransformer nugetNodeTransformer

    DependencyNode makeDependencyNode(String sourcePath, File nugetExecutable) {
        def outputDirectory = new File(new File(packmanProperties.outputDirectoryPath), 'nuget')
        def sourceDirectory = new File(sourcePath)
        String inspectorExePath = getInspectorExePath(sourceDirectory, outputDirectory, nugetExecutable)

        if (!inspectorExePath) {
            return null
        }

        def options =  [
            "--target_path=${sourcePath}",
            "--output_directory=${outputDirectory.getAbsolutePath()}",
            "--ignore_failure=${packmanProperties.inspectorIgnoreFailure}"
        ]
        if(packmanProperties.inspectorExcludedModules) {
            options += "--excluded_modules=${packmanProperties.inspectorExcludedModules}"
        }
        if(logger.traceEnabled) {
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
        File inspectorVersionDirectory = new File(outputDirectory, "${packmanProperties.inspectorPackageName}.${packmanProperties.inspectorPackageVersion}")
        File toolsDirectory = new File(inspectorVersionDirectory, 'tools')
        File inspectorExe = new File(toolsDirectory, "${packmanProperties.inspectorPackageName}.exe")

        //if we can't find the inspector where we expect to, attempt to install it from nuget.org
        if (inspectorExe == null || !inspectorExe.exists()) {
            installInspectorFromNugetDotOrg(sourceDirectory, outputDirectory, nugetExecutable)
            inspectorExe = new File(toolsDirectory, "${packmanProperties.inspectorPackageName}.exe")
        }

        if (inspectorExe == null || !inspectorExe.exists()) {
            logger.error("Could not find the ${packmanProperties.inspectorPackageName} version:${packmanProperties.inspectorPackageVersion} even after an install attempt.")
            return null
        }

        return inspectorExe.absolutePath
    }

    private ExecutableOutput installInspectorFromNugetDotOrg(File sourceDirectory, File outputDirectory, File nugetExecutable) {
        def options =  [
            'install',
            packmanProperties.inspectorPackageName,
            '-Version',
            packmanProperties.inspectorPackageVersion,
            '-OutputDirectory',
            outputDirectory.absolutePath
        ]

        Executable installExecutable = new Executable(sourceDirectory, nugetExecutable.absolutePath, options)
        executableRunner.executeLoudly(installExecutable)
    }
}