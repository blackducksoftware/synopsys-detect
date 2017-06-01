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
package com.blackducksoftware.integration.hub.packman.packagemanager.pip

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.executable.Executable
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunner
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunnerException

@Component
class PipPackager {
    final Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    FileFinder fileFinder

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    PackmanProperties packmanProperties

    @Value('${packman.pip.name}')
    String pipInspectorName

    @Value('${packman.pip.version}')
    String pipInspectorVersion

    @Value('${packman.pip.requirements.path}')
    String requirementsFilePath

    List<DependencyNode> makeDependencyNodes(final String sourcePath, final String pipExecutable, final String pythonExecutable,
            final Map<String, String> environmentVariables) throws ExecutableRunnerException {
        def sourceDirectory = new File(sourcePath)
        def outputDirectory = new File(packmanProperties.outputDirectoryPath)
        def tempDirectory = new File(outputDirectory, UUID.randomUUID().toString())
        tempDirectory.mkdir()
        def requirementsFile = null
        if (requirementsFile) {
            requirementsFile = new File(requirementsFilePath)
        }

        def installProject = new Executable(sourceDirectory, environmentVariables, pythonExecutable, ['setup.py', 'install'])
        executableRunner.executeLoudly(installProject)

        def installPipInspector = new Executable(sourceDirectory, environmentVariables, pipExecutable, [
            'install',
            '-I',
            "${pipInspectorName}==${pipInspectorVersion}"
        ])
        executableRunner.executeLoudly(installPipInspector)

        def pipInspectorOptions = [
            '-u',
            'setup.py',
            'integration_pip_inspector',
            "--OutputDirectory=${tempDirectory.absolutePath}",
            '--IgnoreFailure=False'
        ]
        if(requirementsFile) {
            pipInspectorOptions += "--RequirementsFile=${requirementsFile.absolutePath}"
        }
        def pipInspector = new Executable(sourceDirectory, environmentVariables, pythonExecutable, pipInspectorOptions)

        if(requirementsFile) {
            def installRequirements = new Executable(sourceDirectory, environmentVariables, pipExecutable, [
                'install',
                '-r',
                requirementsFile.absolutePath
            ])
            executableRunner.executeLoudly(installRequirements)
        }

        def failed = executableRunner.executeLoudly(pipInspector)


        String inspectorOutput = fileFinder.findFile(tempDirectory, 'dependencyTree.txt').text
        def parser = new PipInspectorTreeParser()
        DependencyNode project = parser.parse(inspectorOutput)
        tempDirectory.deleteDir()

        [project]
    }
}
