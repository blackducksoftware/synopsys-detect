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
import com.blackducksoftware.integration.hub.detect.DetectProperties
import com.blackducksoftware.integration.hub.detect.bomtool.NpmBomTool
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.stream.JsonReader

@Component
class NpmCliDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(NpmCliDependencyFinder.class)
    private String NPM = 'npm-temp'

    @Autowired
    Gson gson

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    NameVersionNodeTransformer nodeTransformer

    @Autowired
    DetectProperties detectProperties

    public DependencyNode generateDependencyNode(String rootDirectoryPath, String exePath) {
        DependencyNode dependencyNode = null

        def npmLsExe = new Executable(new File(rootDirectoryPath), exePath, ['ls', '-json'])
        def exeRunner = new ExecutableRunner()

        String directoryName = String.format('%s' + File.separator + '%s', detectProperties.getOutputDirectoryPath(), NPM)
        def npmDirectory = new File(directoryName)
        npmDirectory.mkdir()
        npmDirectory.deleteOnExit()

        def npmLsOutFile = new File(npmDirectory, NpmBomTool.OUTPUT_FILE)
        npmLsOutFile.deleteOnExit()

        exeRunner.executeToFile(npmLsExe, npmLsOutFile)

        if (npmLsOutFile?.length() > 0) {
            logger.info("Running npm ls and generating results")
            dependencyNode = convertNpmJsonFileToDependencyNode2(npmLsOutFile, rootDirectoryPath)
        }

        dependencyNode
    }

    private DependencyNode convertNpmJsonFileToDependencyNode(File depOut, String rootPath) {
        NpmCliNode node = gson.fromJson(new JsonReader(new FileReader(depOut)), NpmCliNode.class)

        node.name = projectInfoGatherer.getDefaultProjectName(BomToolType.NPM, rootPath, node.name)
        node.version = projectInfoGatherer.getDefaultProjectVersionName(node.version)

        nodeTransformer.createDependencyNode(Forge.NPM, node)
    }

    private DependencyNode convertNpmJsonFileToDependencyNode2(File depOut, String rootPath) {
        JsonObject npmJson = new JsonParser().parse(new JsonReader(new FileReader(depOut))).getAsJsonObject()

        JsonPrimitive projectName = npmJson.getAsJsonPrimitive('name')
        JsonPrimitive projectVersion = npmJson.getAsJsonPrimitive('version')

        JsonObject dependenciesElement = npmJson.getAsJsonObject('dependencies')

        createNpmNodeFromJsonObject(
                projectInfoGatherer.getDefaultProjectName(BomToolType.NPM, rootPath, projectName?.getAsString()),
                projectInfoGatherer.getDefaultProjectVersionName(projectVersion?.getAsString()),
                dependenciesElement
                )
    }

    private DependencyNode createNpmNodeFromJsonObject(String nodeName, String nodeVersion, JsonObject nodeChildren) {
        def externalId = new NameVersionExternalId(Forge.NPM, nodeName, nodeVersion)
        DependencyNode newNode = new DependencyNode(nodeName, nodeVersion, externalId)

        def elements = nodeChildren?.entrySet()
        elements?.each{
            String name = it.key
            String version = it.value.getAsJsonPrimitive('version').getAsString()
            JsonObject children = it.value.getAsJsonObject('dependencies')

            newNode.children.add(createNpmNodeFromJsonObject(name, version, children))
        }

        newNode
    }
}
