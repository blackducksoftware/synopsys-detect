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
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
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

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    List<DependencyNode> makeDependencyNodes(File outputDirectory, File sourceDirectory, VirtualEnvironment virtualEnv) throws ExecutableRunnerException {
        String pipPath = virtualEnv.pipPath
        String pythonPath = virtualEnv.pythonPath
        def setupFile = fileFinder.findFile(sourceDirectory, 'setup.py')

        String inpsectorScriptContents = getClass().getResourceAsStream("/${INSPECTOR_NAME}.py").getText(StandardCharsets.UTF_8.name())
        File inpsectorScript = new File(outputDirectory, "${INSPECTOR_NAME}.py")
        inpsectorScript.delete()
        inpsectorScript.deleteOnExit()
        inpsectorScript << inpsectorScriptContents
        def pipInspectorOptions = [
            inpsectorScript.absolutePath
        ]

        // Install pytest-runner to avoid a zip_flag error if the project uses pytest-runner
        def installPytestRunner = new Executable(sourceDirectory, pipPath, ['install', 'pytest-runner'])
        executableRunner.executeLoudly(installPytestRunner)

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
        if (setupFile) {
            def installProjectExecutable = new Executable(sourceDirectory, pipPath, ['install', '.', '-I'])
            executableRunner.executeLoudly(installProjectExecutable)
            def projectName = detectProperties.pipProjectName
            if (!projectName) {
                def findProjectNameExecutable = new Executable(sourceDirectory, pythonPath, [
                    setupFile.absolutePath,
                    '--name'
                ])
                projectName = executableRunner.executeQuietly(findProjectNameExecutable).standardOutput.trim()
            }
            pipInspectorOptions += ['-p', projectName]
        }

        def pipInspector = new Executable(sourceDirectory, pythonPath, pipInspectorOptions)
        def inspectorOutput = executableRunner.executeQuietly(pipInspector).standardOutput
        def parser = new PipInspectorTreeParser()
        DependencyNode project = parser.parse(nameVersionNodeTransformer, inspectorOutput)

        if (project.name == PipInspectorTreeParser.UNKOWN_PROJECT_NAME && project.version == PipInspectorTreeParser.UNKOWN_PROJECT_VERSION) {
            project.name = projectInfoGatherer.getDefaultProjectName(BomToolType.PIP, sourceDirectory.getAbsolutePath())
            project.version = projectInfoGatherer.getDefaultProjectVersionName()
            project.externalId = new NameVersionExternalId(Forge.PYPI, project.name, project.version)
        }

        [project]
    }
}
