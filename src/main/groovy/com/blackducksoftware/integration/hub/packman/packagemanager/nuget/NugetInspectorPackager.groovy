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

    @Value('${packman.nuget.inspector.name}')
    String inspectorPackageName

    @Value('${packman.nuget.inspector.version}')
    String inspectorPackageVersion

    @PostConstruct
    void init() {
        inspectorPackageName = inspectorPackageName.trim()
        inspectorPackageVersion = inspectorPackageVersion.trim()
    }

    DependencyNode makeDependencyNode(String sourcePath, Executable nuget) {
        def outputDirectory = new File(packmanProperties.outputDirectoryPath)
        def dependencyNodeFile = new File(outputDirectory, 'dependencyNodes.json')
        def nugetFolder = new File(outputDirectory, "/${inspectorPackageName}.${inspectorPackageVersion}/tools")

        Executable hubNugetInspector = findHubNugetInspector(outputDirectory, nugetFolder, nuget)
        if(!hubNugetInspector) {
            return null
        }

        def commandRunner = new CommandRunner(logger, outputDirectory)
        def hubNugetInspectorCommand = new Command(hubNugetInspector, "--target_path=${sourcePath}", "--output_directory=${outputDirectory.getAbsolutePath()}")
        CommandOutput commandOutput = commandRunner.execute(hubNugetInspectorCommand)
        if(commandOutput.hasErrors()) {
            logger.info('Something went wrong when running HubNugetInspector')
            return null
        }
        dependencyNodeFile.delete()

        nugetNodeTransformer.parse(dependencyNodeFile)
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
