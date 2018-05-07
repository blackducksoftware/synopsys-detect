/*
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager

import groovy.transform.TypeChecked

@Component
@TypeChecked
class PipBomTool extends BomTool<PipApplicableResult> {
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

    PipApplicableResult isBomToolApplicable(File directory) {
        File setupTools = detectFileFinder.findFile(directory, SETUP_FILE_NAME)
        File requirements = null;
        if (detectConfiguration.requirementsFilePath) {
            requirements = new File(detectConfiguration.requirementsFilePath)
        }
        if (setupTools || requirements) {
            ExecutableType pythonType = detectConfiguration.pythonThreeOverride ? ExecutableType.PYTHON3 : ExecutableType.PYTHON;
            ExecutableType pipType = detectConfiguration.pythonThreeOverride ? ExecutableType.PIP3 : ExecutableType.PIP;

            String pythonVersion = detectConfiguration.pythonThreeOverride ? "PYTHON3" : "PYTHON"
            String pipVersion = detectConfiguration.pythonThreeOverride ? "PIP3" : "PIP"

            String pythonExe = executableManager.getExecutablePathOrOverride(ExecutableType.PYTHON3, true, directory, detectConfiguration.pythonPath)
            String pipExe = executableManager.getExecutablePath(ExecutableType.PIP3, true, directory.toString())

            if (pythonExe && pipExe) {
                return new PipApplicableResult(directory, setupTools, requirements, pipExe, pythonExe);
            }else if (!pythonExe) {
                logger.warn("Could not find a ${pythonVersion} executable")
            } else if (!pipExe) {
                logger.warn("Could not find a ${pipVersion} executable")
            }
        }

        return null;
    }

    BomToolExtractionResult extractDetectCodeLocations(PipApplicableResult applicable) {
        File outputDirectory = detectFileManager.createDirectory(BomToolType.PIP)
        File setupFile = applicable.setupTools;
        File inspectorScript = pipInspectorManager.extractInspectorScript()
        def projectName = findProjectName(applicable);
        String inspectorOutput = pipInspectorManager.runInspector(applicable.directory, applicable.pythonExe.toString(), inspectorScript, projectName, applicable.requirements.toString())
        def codeLocation = pipInspectorTreeParser.parse(inspectorOutput, applicable.directoryString)

        def codeLocations = []
        if (codeLocation != null) {
            codeLocations.add(codeLocation)
        }

        bomToolExtractionResultsFactory.fromCodeLocations(codeLocations, getBomToolType(), applicable.directory)
    }

    String findProjectName(PipApplicableResult applicable) {
        def projectName = detectConfiguration.pipProjectName
        if (applicable.setupTools) {
            if (!projectName) {
                def findProjectNameExecutable = new Executable(applicable.directory, applicable.pythonExe, [
                    applicable.directory.absolutePath,
                    '--name'
                ])
                List<String> output = executableRunner.execute(findProjectNameExecutable).standardOutputAsList
                projectName = output.get(output.size() - 1).replace('_', '-').trim()
            }
        }

        projectName
    }
}
