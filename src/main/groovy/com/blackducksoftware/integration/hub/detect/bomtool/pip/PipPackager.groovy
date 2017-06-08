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
package com.blackducksoftware.integration.hub.detect.bomtool.pip

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectProperties
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.FileFinder
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException

@Component
class PipPackager {
    final Logger logger = LoggerFactory.getLogger(this.getClass())
    private final String INSPECTOR_NAME = 'pip-inspector'

    @Autowired
    FileFinder fileFinder

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    DetectProperties detectProperties

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    List<DependencyNode> makeDependencyNodes(final String sourcePath, VirtualEnvironment virtualEnv) throws ExecutableRunnerException {
        String pipPath = virtualEnv.pipPath
        String pythonPath = virtualEnv.pythonPath
        def sourceDirectory = new File(sourcePath)
        def outputDirectory = new File(detectProperties.outputDirectoryPath)
        def setupFile = fileFinder.findFile(sourceDirectory, 'setup.py')

        File inpsectorScript = File.createTempFile(INSPECTOR_NAME, '.py')
        String inpsectorScriptContents = getClass().getResourceAsStream("/${INSPECTOR_NAME}.py").getText(StandardCharsets.UTF_8.name())
        inpsectorScript << inpsectorScriptContents
        def pipInspectorOptions = [
            inpsectorScript.absolutePath
        ]

        // Install requirements file and add it as an option for the inspector
        if (detectProperties.requirementsFilePath) {
            def requirementsFile = new File(detectProperties.requirementsFilePath)
            pipInspectorOptions += [
                '-r',
                requirementsFile.absolutePath
            ]

            def installRequirements = new Executable(sourceDirectory, pipPath, [
                'install',
                '-r',
                requirementsFile.absolutePath
            ])
            executableRunner.executeLoudly(installRequirements)
        }

        // Install project if it can find one and pass its name to the inspector
        if(setupFile) {
            def installProjectExecutable = new Executable(sourceDirectory, pipPath, ['install', '.', '-I'])
            executableRunner.executeLoudly(installProjectExecutable)

            if(!detectProperties.projectName) {
                def findProjectNameExecutable = new Executable(sourceDirectory, pythonPath, [
                    setupFile.absolutePath,
                    '--name'
                ])
                def projectName = executableRunner.executeQuietly(findProjectNameExecutable).standardOutput.trim()
                pipInspectorOptions += ['-p', projectName]
            }
        }

        def pipInspector = new Executable(sourceDirectory, pythonPath, pipInspectorOptions)
        def inspectorOutput = executableRunner.executeQuietly(pipInspector).standardOutput
        def parser = new PipInspectorTreeParser()
        DependencyNode project = parser.parse(inspectorOutput)

        if(project.name == PipInspectorTreeParser.UNKOWN_PROJECT) {
            project.name = projectInfoGatherer.getDefaultProjectName(BomToolType.PIP, sourcePath)
            project.version = projectInfoGatherer.getDefaultProjectVersionName()
            project.externalId = new NameVersionExternalId(Forge.PYPI, project.name, project.version)
        }

        [project]
    }
}
