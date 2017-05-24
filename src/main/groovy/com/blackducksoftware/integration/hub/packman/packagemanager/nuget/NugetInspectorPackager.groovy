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
package com.blackducksoftware.integration.hub.packman.packagemanager.nuget

import javax.annotation.PostConstruct

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.command.Command
import com.blackducksoftware.integration.hub.packman.util.command.CommandOutput
import com.blackducksoftware.integration.hub.packman.util.command.CommandRunner

@Component
class NugetInspectorPackager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorPackager.class)

    @Autowired
    PackmanProperties packmanProperties

    @Autowired
    FileFinder fileFinder

    @Autowired
    CommandRunner commandRunner

    @Autowired
    NugetNodeTransformer nugetNodeTransformer

    @Value('${packman.nuget.inspector.name}')
    String inspectorPackageName

    @Value('${packman.nuget.inspector.version}')
    String inspectorPackageVersion

    @Value('${packman.nuget.excluded.modules}')
    String inspectorExcludedModules

    @Value('${packman.nuget.ingore.failure}')
    boolean inspectorIgnoreFailure

    @PostConstruct
    void init() {
        inspectorPackageName = inspectorPackageName.trim()
        inspectorPackageVersion = inspectorPackageVersion.trim()
    }

    DependencyNode makeDependencyNode(String sourcePath, File nugetCommand) {
        def outputDirectory = new File(packmanProperties.outputDirectoryPath)
        def sourceDirectory = new File(sourcePath)
        String inspectorExePath = getInspectorExePath(sourceDirectory, outputDirectory, nugetCommand)

        if (!inspectorExePath) {
            return null
        }

        String[] options =  [
            "--target_path=${sourcePath}",
            "--output_directory=${outputDirectory.getAbsolutePath()}",
            "--ignore_failure=${inspectorIgnoreFailure}"
        ]
        if(inspectorExcludedModules) {
            options += "--excluded_modules=${inspectorExcludedModules}"
        }
        if(logger.traceEnabled) {
            options += "-v"
        }

        def hubNugetInspectorCommand = new Command(sourceDirectory, inspectorExePath, options)
        CommandOutput commandOutput = commandRunner.executeLoudly(hubNugetInspectorCommand)

        def dependencyNodeFile = fileFinder.findFile(outputDirectory, '*_dependency_node.json')
        DependencyNode node = nugetNodeTransformer.parse(dependencyNodeFile)
        dependencyNodeFile.delete()
        return node
    }

    private String getInspectorExePath(File sourceDirectory, File outputDirectory, File nugetCommand) {
        File inspectorVersionDirectory = new File(outputDirectory, "${inspectorPackageName}.${inspectorPackageVersion}")
        File toolsDirectory = new File(inspectorVersionDirectory, 'tools')
        File inspectorExe = new File(toolsDirectory, "${inspectorPackageName}.exe")

        //if we can't find the inspector where we expect to, attempt to install it from nuget.org
        if (inspectorExe == null || !inspectorExe.exists()) {
            installInspectorFromNugetDotOrg(sourceDirectory, outputDirectory, nugetCommand)
            inspectorExe = new File(toolsDirectory, "${inspectorPackageName}.exe")
        }

        if (inspectorExe == null || !inspectorExe.exists()) {
            logger.error("Could not find the ${inspectorPackageName} version:${inspectorPackageVersion} even after an install attempt.")
            return null
        }

        return inspectorExe.absolutePath
    }

    private CommandOutput installInspectorFromNugetDotOrg(File sourceDirectory, File outputDirectory, File nugetCommand) {
        Command installCommand = new Command(sourceDirectory, nugetCommand.absolutePath, 'install', inspectorPackageName, '-Version', inspectorPackageVersion, '-OutputDirectory', outputDirectory.absolutePath)
        commandRunner.executeLoudly(installCommand)
    }
}
