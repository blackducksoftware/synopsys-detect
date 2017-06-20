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
package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.NpmBomTool
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader

@Component
class NpmCliDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(NpmCliDependencyFinder.class)
    private final String NPM_DIR = 'npm-temp'
    private final String JSON_NAME = 'name'
    private final String JSON_VERSION = 'version'
    private final String JSON_DEPENDENCIES = 'dependencies'

    @Autowired
    Gson gson

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    NameVersionNodeTransformer nodeTransformer

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    DetectFileManager detectFileManager

    public DependencyNode generateDependencyNode(String rootDirectoryPath, String exePath) {
        def npmLsExe = new Executable(new File(rootDirectoryPath), exePath, ['ls', '-json'])
        def exeRunner = new ExecutableRunner()

        File npmLsOutFile = detectFileManager.createFile(BomToolType.NPM, NpmBomTool.OUTPUT_FILE)

        exeRunner.executeToFile(npmLsExe, npmLsOutFile)

        if (npmLsOutFile?.length() > 0) {
            logger.info("Running npm ls and generating results")
            return convertNpmJsonFileToDependencyNode(npmLsOutFile, rootDirectoryPath)
        } else {
            logger.error("Ran into an issue creating and writing to file")
        }

        null
    }

    private DependencyNode convertNpmJsonFileToDependencyNode(File NpmLsOutFile, String projectRootPath) {
        JsonObject npmJson = new JsonParser().parse(new JsonReader(new FileReader(NpmLsOutFile))).getAsJsonObject()

        String name = npmJson.getAsJsonPrimitive(JSON_NAME)?.getAsString()
        String version = npmJson.getAsJsonPrimitive(JSON_VERSION)?.getAsString()

        String projectName = projectInfoGatherer.getDefaultProjectName(BomToolType.NPM, projectRootPath, name)
        String projectVersion = projectInfoGatherer.getDefaultProjectVersionName(version)

        def externalId = new NameVersionExternalId(Forge.NPM, projectName, projectVersion)
        def dependencyNode = new DependencyNode(projectName, projectVersion, externalId)

        createDependencyNodeTreeFromJsonObject(dependencyNode, npmJson.getAsJsonObject(JSON_DEPENDENCIES))

        dependencyNode
    }

    private void createDependencyNodeTreeFromJsonObject(DependencyNode parentDependencyNode, JsonObject parentNodeChildren) {
        def elements = parentNodeChildren?.entrySet()
        elements?.each {
            String name = it.key
            String version = it.value.getAsJsonPrimitive(JSON_VERSION)?.getAsString()
            JsonObject children = it.value.getAsJsonObject(JSON_DEPENDENCIES)

            def externalId = new NameVersionExternalId(Forge.NPM, name, version)
            def newNode = new DependencyNode(name, version, externalId)

            createDependencyNodeTreeFromJsonObject(newNode, children)
            parentDependencyNode.children.add(newNode)
        }
    }
}
