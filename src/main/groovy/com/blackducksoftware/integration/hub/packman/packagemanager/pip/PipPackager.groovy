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

    @Value('packman.output.path')
    String outputPath

    @Value('packman.pip.requirements.path')
    String requirementsPath

    @Value('packman.pip.inspector.name')
    String pipInspectorName

    @Value('packman.pip.inspector.version')
    String pipInspectorVersion

    List<DependencyNode> makeDependencyNodes(final String sourcePath, final String pipExecutable, final String pythonExecutable,
            final Map<String, String> environmentVariables) throws ExecutableRunnerException {
        def sourceDirectory = new File(sourcePath)
        def outputDirectory = new File(outputPath)
        def requirementsFile = new File(requirementsPath)

        def installProject = new Executable(sourceDirectory, environmentVariables, pipExecutable, ['install', '.'])
        def installPipInspector = new Executable(sourceDirectory, environmentVariables, pipExecutable, [
            'install',
            "${pipInspectorName}==${pipInspectorVersion}"
        ])

        def pipInspectorOptions = [
            'setup.py',
            'integration_pip_inspector',
            "--OutputDirectory=${outputDirectory.absolutePath}",
            '--IgnoreFailure=False',
            '--CreateTreeDependencyList=True'
        ]
        if(requirementsFile) {
            pipInspectorOptions += "--RequirementsFile=${requirementsFile.absolutePath}"
        }
        def pipInspector = new Executable(sourceDirectory, environmentVariables, pythonExecutable, pipInspectorOptions)

        executableRunner.executeLoudly(installProject)
        executableRunner.executeLoudly(installPipInspector)
        executableRunner.executeLoudly(pipInspector)

        String inspectorOutput = fileFinder.findFile(outputDirectory, '*_blackduck_tree.txt').text
        def parser = new PipInspectorTreeParser()
        DependencyNode project = parser.parse(inspectorOutput)

        [project]
    }
}
