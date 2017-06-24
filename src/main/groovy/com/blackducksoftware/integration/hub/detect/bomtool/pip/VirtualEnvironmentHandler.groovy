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

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
class VirtualEnvironmentHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())
    private final String VIRTUAL_ENV_NAME = 'venv'

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExecutableManager executableManager

    @Autowired
    ExecutableRunner executableRunner

    VirtualEnvironment systemEnvironment

    String binFolderName

    void init() {
        systemEnvironment = new VirtualEnvironment()
        ExecutableType pipExecutableType
        ExecutableType pythonExecutableType

        if (detectConfiguration.getPipThreeOverride()) {
            pythonExecutableType = ExecutableType.PYTHON3
            pipExecutableType = ExecutableType.PIP3
        } else {
            pythonExecutableType = ExecutableType.PYTHON
            pipExecutableType = ExecutableType.PIP
        }

        systemEnvironment.pythonType = pythonExecutableType
        systemEnvironment.pipType = pipExecutableType
        systemEnvironment.pythonPath = findExecutable(null, detectConfiguration.pythonPath, pythonExecutableType)
        systemEnvironment.pipPath = findExecutable(null, detectConfiguration.pipPath, pipExecutableType)

        if (SystemUtils.IS_OS_WINDOWS) {
            binFolderName = 'Scripts'
        } else {
            binFolderName = 'bin'
        }
    }

    VirtualEnvironment getVirtualEnvironment(File outputDirectory) {
        VirtualEnvironment env = getSystemEnvironment()
        String definedPath = detectConfiguration.virtualEnvPath?.trim()
        if (detectConfiguration.createVirtualEnv) {
            def venvDirectory = new File(outputDirectory, VIRTUAL_ENV_NAME)
            env = findExistingEnvironment(venvDirectory)

            def definedEnv = null
            if (definedPath) {
                definedEnv = new File(definedPath)
                env = findExistingEnvironment(definedEnv)
            }

            if (!env && definedEnv) {
                env = createVirtualEnvironment(definedEnv)
            } else if (!env) {
                env = createVirtualEnvironment(venvDirectory)
            }
        } else if (definedPath) {
            def venvDirectory = new File(detectConfiguration.virtualEnvPath)
            env = findExistingEnvironment(venvDirectory)
        }

        if (!env) {
            logger.warn('Failed to get/create any virtual environment')
        }

        env
    }

    VirtualEnvironment createVirtualEnvironment(File virtualEnvDirectory) {
        def installVirtualEnvPackage = new Executable(virtualEnvDirectory.getParentFile(), systemEnvironment.pipPath, ['install', 'virtualenv'])
        executableRunner.execute(installVirtualEnvPackage)

        def virtualEnvOptions = [
            "--python=${systemEnvironment.getPythonPath()}",
            virtualEnvDirectory.absolutePath
        ]
        def virtualEnvPath = executableManager.getPathOfExecutable(ExecutableType.VIRTUALENV)
        def setupVirtualEnvironment = new Executable(virtualEnvDirectory.getParentFile(), virtualEnvPath, virtualEnvOptions)
        executableRunner.execute(setupVirtualEnvironment)

        findExistingEnvironment(virtualEnvDirectory)
    }

    VirtualEnvironment findExistingEnvironment(File virtualEnvDirectory) {
        VirtualEnvironment existing = null

        String pythonName = systemEnvironment.pythonType.getExecutable(executableManager.currentOs)
        String pipName = systemEnvironment.pipType.getExecutable(executableManager.currentOs)
        def virtualEnvPython = new File(virtualEnvDirectory, "${binFolderName}/${pythonName}")
        def virtualEnvPip = new File(virtualEnvDirectory, "${binFolderName}/${pipName}")

        if (virtualEnvDirectory.exists() && virtualEnvDirectory.isDirectory() && virtualEnvPython.exists() && virtualEnvPip.exists()) {
            existing = new VirtualEnvironment()
            existing.pythonType = systemEnvironment.pythonType
            existing.pipType = systemEnvironment.pipType
            existing.pythonPath = virtualEnvPython.absolutePath
            existing.pipPath = virtualEnvPip.absolutePath
        }

        existing
    }

    private String findExecutable(String path, String executablePath, ExecutableType commandType) {
        if (StringUtils.isNotBlank(executablePath)) {
            executablePath
        } else {
            if (StringUtils.isBlank(path)) {
                executableManager.getPathOfExecutable(commandType)
            } else {
                executableManager.getPathOfExecutable(path, commandType)
            }
        }
    }
}
