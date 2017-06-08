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

import javax.annotation.PostConstruct

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.SystemUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectProperties
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
class VirtualEnvironmentHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())
    private final String VIRTUAL_ENV_NAME = 'venv'

    @Autowired
    DetectProperties detectProperties

    @Autowired
    ExecutableManager executableManager

    @Autowired
    ExecutableRunner executableRunner

    VirtualEnvironment systemEnvironment

    String binFolderName

    @PostConstruct
    void init() {
        systemEnvironment = new VirtualEnvironment()
        ExecutableType pipExecutableType
        ExecutableType pythonExecutableType

        if (detectProperties.getPipThreeOverride()) {
            pythonExecutableType = ExecutableType.PYTHON3
            pipExecutableType = ExecutableType.PIP3
        } else {
            pythonExecutableType = ExecutableType.PYTHON
            pipExecutableType = ExecutableType.PIP
        }

        systemEnvironment.pythonType = pythonExecutableType
        systemEnvironment.pipType = pipExecutableType
        systemEnvironment.pythonPath = findExecutable(null, detectProperties.pythonPath, pythonExecutableType)
        systemEnvironment.pipPath = findExecutable(null, detectProperties.pipPath, pipExecutableType)

        if (SystemUtils.IS_OS_WINDOWS) {
            binFolderName = 'Scripts'
        } else {
            binFolderName = 'bin'
        }
    }

    VirtualEnvironment getVirtualEnvironment(File sourceDirectory) {
        VirtualEnvironment env = null
        def outputDirectory = new File(detectProperties.outputDirectoryPath)
        String definedPath = detectProperties.virtualEnvPath?.trim()
        if(detectProperties.createVirtualEnv) {
            def venvDirectory = new File(outputDirectory, VIRTUAL_ENV_NAME)
            env = findExistingEnvironment(venvDirectory)

            def definedEnv = null
            if(definedPath) {
                definedEnv = new File(definedPath)
                env = findExistingEnvironment(definedEnv)
            }

            if(!env && definedEnv){
                env = createVirtualEnvironment(definedEnv)
            } else if (!env) {
                env = createVirtualEnvironment(venvDirectory)
            }
        } else if (definedPath){
            def venvDirectory = new File(detectProperties.virtualEnvPath)
            env = findExistingEnvironment(venvDirectory)
        } else if (detectProperties.createVirtualEnv && definedPath){
            env = getSystemEnvironment()
        }

        if(!env) {
            logger.warn('Failed to get/create any virtual environment')
        }

        env
    }

    VirtualEnvironment createVirtualEnvironment(File virtualEnvDirectory) {
        def installVirtualEnvPackage = new Executable(virtualEnvDirectory.getParentFile(), systemEnvironment.pipPath, ['install', 'virtualenv'])
        executableRunner.executeLoudly(installVirtualEnvPackage)

        def virtualEnvOptions = [
            "--python=${systemEnvironment.getPythonPath()}",
            virtualEnvDirectory.absolutePath
        ]
        def virtualEnvPath = executableManager.getPathOfExecutable(ExecutableType.VIRTUALENV)
        def setupVirtualEnvironment = new Executable(virtualEnvDirectory.getParentFile(), virtualEnvPath, virtualEnvOptions)
        executableRunner.executeLoudly(setupVirtualEnvironment)

        findExistingEnvironment(virtualEnvDirectory)
    }

    VirtualEnvironment findExistingEnvironment(File virtualEnvDirectory) {
        VirtualEnvironment existing = null

        String pythonName = systemEnvironment.pythonType.getExecutable(executableManager.currentOs)
        String pipName = systemEnvironment.pipType.getExecutable(executableManager.currentOs)
        def virtualEnvPython = new File(virtualEnvDirectory, "${binFolderName}/${pythonName}")
        def virtualEnvPip = new File(virtualEnvDirectory, "${binFolderName}/${pipName}")

        if(virtualEnvDirectory.exists() && virtualEnvDirectory.isDirectory() && virtualEnvPython.exists() && virtualEnvPip.exists()) {
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
            if(StringUtils.isBlank(path)){
                executableManager.getPathOfExecutable(commandType)
            } else {
                executableManager.getPathOfExecutable(path, commandType)
            }
        }
    }
}
