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

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

import groovy.transform.TypeChecked

@Component
@TypeChecked
class PipInspectorManager {
    final Logger logger = LoggerFactory.getLogger(PipInspectorManager.class)

    public static final String INSPECTOR_NAME = 'pip-inspector.py'

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    DetectFileManager detectFileManager

    File extractInspectorScript() {
        String inpsectorScriptContents = getClass().getResourceAsStream("/${INSPECTOR_NAME}").getText(StandardCharsets.UTF_8.toString())
        def inspectorScript = detectFileManager.createFile(BomToolType.PIP, INSPECTOR_NAME)
        logger.info("INSPECTOR SCRIPT : "  + inspectorScript);
        detectFileManager.writeToFile(inspectorScript, inpsectorScriptContents)
    }

    String runInspector(File sourceDirectory, String pythonPath, File inspectorScript, String projectName, String requirementsFilePath) {
        List<String> inspectorArguments = [
            inspectorScript.absolutePath
        ]

        if (requirementsFilePath) {
            def requirementsFile = new File(requirementsFilePath)
            logger.info("Requirements File : "  + requirementsFile);
            inspectorArguments.add("--requirements=${requirementsFile.absolutePath}" as String)
        }

        if (projectName) {
            inspectorArguments.add("--projectname=${projectName}" as String)
        }

        def pipInspector = new Executable(sourceDirectory, pythonPath, inspectorArguments)
        executableRunner.execute(pipInspector).standardOutput
    }
}
