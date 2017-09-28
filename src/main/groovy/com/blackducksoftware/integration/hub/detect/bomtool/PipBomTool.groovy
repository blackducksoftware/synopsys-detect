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
package com.blackducksoftware.integration.hub.detect.bomtool

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorManager
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorTreeParser
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager

import groovy.transform.TypeChecked

@Component
@TypeChecked
class PipBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(PipBomTool.class)

    private final String SETUP_FILE_NAME = 'setup.py'

    @Autowired
    PipInspectorManager pipInspectorManager

    @Autowired
    PipInspectorTreeParser pipInspectorTreeParser

    @Autowired
    ExecutableManager executableManager

    @Autowired
    DetectConfiguration detectConfiguration

    BomToolType getBomToolType() {
        BomToolType.PIP
    }

    boolean isBomToolApplicable() {
        boolean hasSetupToolsFile = detectFileManager.containsAllFiles(sourcePath, SETUP_FILE_NAME)
        boolean hasRequirementsFile = detectConfiguration.requirementsFilePath

        def hasExecutables
        if (hasSetupToolsFile || hasRequirementsFile) {
            boolean hasPython = getPythonPath()
            String pythonVersion = detectConfiguration.pythonThreeOverride ? "PYTHON" : "PYTHON3"
            String pipVersion = detectConfiguration.pythonThreeOverride ? "PIP" : "PIP3"

            if (!hasPython) {
                logger.warn("Could not find a ${pythonVersion} executable")
            }

            boolean hasPip
            if(detectConfiguration.pythonThreeOverride) {
                hasPip = executableManager.getExecutablePath(ExecutableType.PIP3, true, detectConfiguration.sourcePath)
            } else {
                hasPip = executableManager.getExecutablePath(ExecutableType.PIP, true, detectConfiguration.sourcePath)
            }

            if (!hasPip) {
                logger.warn("Could not find a ${pipVersion} executable")
            }

            hasExecutables = hasPython && hasPip
        }

        hasExecutables && (hasSetupToolsFile || hasRequirementsFile)
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        File outputDirectory = detectFileManager.createDirectory(BomToolType.PIP)
        File setupFile = detectFileManager.findFile(sourceDirectory, SETUP_FILE_NAME)
        File inspectorScript = pipInspectorManager.extractInspectorScript()
        String inspectorOutput = pipInspectorManager.runInspector(sourceDirectory, pythonPath, inspectorScript, projectName, detectConfiguration.requirementsFilePath)
        DependencyNode projectNode = pipInspectorTreeParser.parse(inspectorOutput)

        def codeLocations = []
        if (projectNode && !(projectNode.name.equals('') && projectNode.version.equals('') && projectNode.children.empty)) {
            def codeLocation = new DetectCodeLocation(BomToolType.PIP, sourcePath, projectNode)
            codeLocations.add(codeLocation)
        }

        codeLocations
    }

    String getProjectName() {
        def projectName = detectConfiguration.pipProjectName
        def setupFile = detectFileManager.findFile(sourceDirectory, SETUP_FILE_NAME)
        if (setupFile) {
            if (!projectName) {
                def findProjectNameExecutable = new Executable(sourceDirectory, pythonPath, [
                    setupFile.absolutePath,
                    '--name'
                ])
                String[] output = executableRunner.execute(findProjectNameExecutable).standardOutput.split('\n')
                projectName = output[output.length - 1].replace('_', '-').trim()
            }
        }

        projectName
    }

    String getPythonPath() {
        def pythonPath = detectConfiguration.pythonPath

        if (detectConfiguration.pythonThreeOverride){
            pythonPath = executableManager.getExecutablePath(ExecutableType.PYTHON3, true, detectConfiguration.sourcePath)
        } else if (!pythonPath?.trim()) {
            pythonPath = executableManager.getExecutablePath(ExecutableType.PYTHON, true, detectConfiguration.sourcePath)
        }

        pythonPath
    }
}
