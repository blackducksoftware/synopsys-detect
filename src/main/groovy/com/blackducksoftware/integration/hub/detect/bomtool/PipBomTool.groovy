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

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.pip.PipInspectorTreeParser
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PythonEnvironment
import com.blackducksoftware.integration.hub.detect.bomtool.pip.PythonEnvironmentHandler
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.util.executable.Executable

import groovy.transform.TypeChecked

@Component
@TypeChecked
class PipBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(PipBomTool.class)

    private final String INSPECTOR_NAME = 'pip-inspector.py'
    private final String SETUP_FILE_NAME = 'setup.py'

    @Autowired
    PipInspectorTreeParser pipInspectorTreeParser

    @Autowired
    PythonEnvironmentHandler virtualEnvironmentHandler

    BomToolType getBomToolType() {
        BomToolType.PIP
    }

    boolean isBomToolApplicable() {
        def containsFiles = detectFileManager.containsAllFiles(sourcePath, SETUP_FILE_NAME)
        def definedRequirements = detectConfiguration.requirementsFilePath

        def foundExecutables
        if (containsFiles || definedRequirements) {
            virtualEnvironmentHandler.init()
            PythonEnvironment systemEnvironment = virtualEnvironmentHandler.getSystemEnvironment()
            foundExecutables = systemEnvironment.pipPath && systemEnvironment.pythonPath
            if (!systemEnvironment.pipPath) {
                logger.warn("Could not find a ${executableManager.getExecutableName(systemEnvironment.pipType)} executable")
            }
            if (!systemEnvironment.pythonPath) {
                logger.warn("Could not find a ${executableManager.getExecutableName(systemEnvironment.pythonType)} executable")
            }
        }

        foundExecutables && (containsFiles || definedRequirements)
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        def outputDirectory = detectFileManager.createDirectory(BomToolType.PIP)
        def sourcePath = sourcePath

        PythonEnvironment pythonEnvironment = virtualEnvironmentHandler.getEnvironment(detectConfiguration.virtualEnvPath)
        DetectCodeLocation codeLocation = makeCodeLocation(pythonEnvironment)

        [codeLocation]
    }

    DetectCodeLocation makeCodeLocation(PythonEnvironment pythonEnvironment) {
        String pipPath = pythonEnvironment.pipPath
        String pythonPath = pythonEnvironment.pythonPath
        def setupFile = detectFileManager.findFile(sourceDirectory, 'setup.py')

        String inpsectorScriptContents = getClass().getResourceAsStream("/${INSPECTOR_NAME}").getText(StandardCharsets.UTF_8.toString())
        def inspectorScript = detectFileManager.createFile(BomToolType.PIP, INSPECTOR_NAME)
        detectFileManager.writeToFile(inspectorScript, inpsectorScriptContents)
        def pipInspectorOptions = [inspectorScript.absolutePath]

        // Install requirements file and add it as an option for the inspector
        if (detectConfiguration.requirementsFilePath) {
            def requirementsFile = new File(detectConfiguration.requirementsFilePath)
            pipInspectorOptions.add("--requirements=${requirementsFile.absolutePath}" as String)
        }

        // Install project if it can find one and pass its name to the inspector
        if (setupFile) {
            def projectName = detectConfiguration.pipProjectName
            if (!projectName) {
                def findProjectNameExecutable = new Executable(sourceDirectory, pythonPath, [setupFile.absolutePath, '--name'])
                String[] output = executableRunner.execute(findProjectNameExecutable).standardOutput.split(System.lineSeparator())
                projectName = output[output.length - 1].replace('_', '-').trim()
            }
            pipInspectorOptions.add("--projectname=${projectName}" as String)
        }

        def pipInspector = new Executable(sourceDirectory, pythonPath, pipInspectorOptions)
        def inspectorOutput = executableRunner.execute(pipInspector).standardOutput

        pipInspectorTreeParser.parse(nameVersionNodeTransformer, sourcePath, inspectorOutput)
    }
}