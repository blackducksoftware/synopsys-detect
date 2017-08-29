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

import com.blackducksoftware.integration.hub.detect.bomtool.npm.NpmCliDependencyFinder
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType

@Component
class NpmBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(NpmBomTool.class)

    public static final String NODE_MODULES = 'node_modules'
    public static final String PACKAGE_JSON = 'package.json'
    public static final String OUTPUT_FILE = 'detect_npm_proj_dependencies.json'
    public static final String ERROR_FILE = 'detect_npm_error.json'

    private String npmExePath

    @Autowired
    NpmCliDependencyFinder cliDependencyFinder

    @Autowired
    YarnBomTool yarnBomTool

    @Autowired
    HubSignatureScanner hubSignatureScanner

    @Override
    public BomToolType getBomToolType() {
        BomToolType.NPM
    }

    @Override
    public boolean isBomToolApplicable() {
        if (yarnBomTool.isBomToolApplicable()) {
            logger.debug("Not running npm bomtool because Yarn is applicable")
            return false
        }

        boolean containsNodeModules = detectFileManager.containsAllFiles(sourcePath, NODE_MODULES)
        boolean containsPackageJson = detectFileManager.containsAllFiles(sourcePath, PACKAGE_JSON)

        if (containsPackageJson && !containsNodeModules) {
            logger.warn("package.json was located in ${sourcePath}, but the node_modules folder was NOT located. Please run 'npm install' in that location and try again.")
        } else if (containsPackageJson && containsNodeModules) {
            npmExePath = findExecutablePath(ExecutableType.NPM, true, detectConfiguration.getNpmPath())
            if (!npmExePath) {
                logger.warn("Could not find a ${executableManager.getExecutableName(ExecutableType.NPM)} executable")
            }
        }

        boolean isApplicable = containsNodeModules && npmExePath
        if (isApplicable) {
            logger.debug("Npm version ${executableRunner.runExe(npmExePath, '-version').standardOutput}")
        }

        isApplicable
    }

    List<DetectCodeLocation> extractDetectCodeLocations() {
        File npmLsOutputFile = detectFileManager.createFile(BomToolType.NPM, NpmBomTool.OUTPUT_FILE)
        File npmLsErrorFile = detectFileManager.createFile(BomToolType.NPM, NpmBomTool.ERROR_FILE)
        executableRunner.runExeToFile(npmExePath, npmLsOutputFile, npmLsErrorFile, 'ls', '-json')

        if (npmLsOutputFile.length() > 0) {
            if (npmLsErrorFile.length() > 0) {
                logger.debug("Error when running npm ls -json command\n${npmLsErrorFile.text}")
            }
            def dependencyNode = cliDependencyFinder.generateDependencyNode(npmLsOutputFile)
            def detectCodeLocation = new DetectCodeLocation(getBomToolType(), sourcePath, dependencyNode)

            hubSignatureScanner.registerPathToScan(sourceDirectory, NODE_MODULES)
            return [detectCodeLocation]
        } else if (npmLsErrorFile.length() > 0) {
            logger.error("Error when running npm ls -json command\n${npmLsErrorFile.text}")
        } else {
            logger.warn("Nothing returned from npm ls -json command")
        }

        []
    }
}
