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
import com.blackducksoftware.integration.hub.packman.help.ValueDescription
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.commands.Command
import com.blackducksoftware.integration.hub.packman.util.commands.CommandOutput
import com.blackducksoftware.integration.hub.packman.util.commands.CommandRunner
import com.blackducksoftware.integration.hub.packman.util.commands.Executable

@Component
class NugetInspectorPackager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorPackager.class)

    @Autowired
    PackmanProperties packmanProperties

    @Autowired
    FileFinder fileFinder

    @Autowired
    NugetNodeTransformer nugetNodeTransformer

    @ValueDescription(key="packman.nuget.inspector.name", description="Name of the Nuget Inspector")
    @Value('${packman.nuget.inspector.name}')
    String inspectorPackageName

    @ValueDescription(key="packman.nuget.inspector.version", description="Version of the Nuget Inspector")
    @Value('${packman.nuget.inspector.version}')
    String inspectorPackageVersion

    @ValueDescription(key="packman.nuget.excluded.modules", description="The names of the projects in a solution to exclude")
    @Value('${packman.nuget.excluded.modules}')
    String inspectorExcludedModules

    @ValueDescription(key="packman.nuget.ignore.failure", description="If true errors will be logged and then ignored.")
    @Value('${packman.nuget.ignore.failure}')
    boolean inspectorIgnoreFailure

    @PostConstruct
    void init() {
        inspectorPackageName = inspectorPackageName.trim()
        inspectorPackageVersion = inspectorPackageVersion.trim()
    }

    DependencyNode makeDependencyNode(String sourcePath, Executable nuget) {
        def outputDirectory = new File(packmanProperties.outputDirectoryPath)
        def nugetFolder = new File(outputDirectory, "/${inspectorPackageName}.${inspectorPackageVersion}/tools")

        Executable hubNugetInspector = findHubNugetInspector(outputDirectory, nugetFolder, nuget)
        if(!hubNugetInspector) {
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

        def hubNugetInspectorCommand = new Command(hubNugetInspector, options)
        def commandRunner = new CommandRunner(logger, outputDirectory)
        CommandOutput commandOutput = commandRunner.execute(hubNugetInspectorCommand)
        if(commandOutput.hasErrors()) {
            logger.info('Something went wrong when running HubNugetInspector')
            return null
        }

        def dependencyNodeFile = fileFinder.findFile(outputDirectory, '*_dependency_node.json')
        DependencyNode node = nugetNodeTransformer.parse(dependencyNodeFile)
        dependencyNodeFile.delete()
        return node
    }

    Executable findHubNugetInspector(File outputDirectory, File nugetFolder, Executable nuget) {
        if(!nugetFolder.exists() && fetchFromNuget(outputDirectory, nuget).hasErrors()) {
            logger.info('Failed to install HubNugetInspector')
        } else {
            return fileFinder.findExecutable('IntegrationNugetInspector.exe', nugetFolder.getAbsolutePath())
        }
        null
    }

    CommandOutput fetchFromNuget(File outputDirectory, Executable nuget) {
        def commandRunner = new CommandRunner(logger, outputDirectory)
        def installCommand = new Command(
                nuget,
                'install', inspectorPackageName,
                '-Version', inspectorPackageVersion,
                '-OutputDirectory', outputDirectory.getAbsolutePath())
        commandRunner.execute(installCommand)
    }
}
