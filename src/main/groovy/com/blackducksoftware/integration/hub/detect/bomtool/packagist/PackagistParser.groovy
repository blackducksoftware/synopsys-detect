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
package com.blackducksoftware.integration.hub.detect.bomtool.packagist

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.PackagistBomTool
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.FileFinder
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader

@Component
class PackagistParser {
    private Forge packagistForge = new Forge('packagist', ':')

    @Autowired
    Gson gson

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    public DependencyNode getDependencyNodeFromProject(String projectPath) {
        File composerLockFile = fileFinder.findFile(projectPath, PackagistBomTool.COMPOSER_LOCK)

        JsonObject composerLockJsonObject = new JsonParser().parse(new JsonReader(new FileReader(composerLockFile))).getAsJsonObject()
        String projectName = projectInfoGatherer.getProjectName(BomToolType.PACKAGIST, projectPath, composerLockJsonObject.get('name')?.getAsString())
        String projectVersion = projectInfoGatherer.getProjectVersionName(composerLockJsonObject.get('version')?.getAsString())

        def rootDependencyNode = new DependencyNode(projectName, projectVersion, new NameVersionExternalId(packagistForge, projectName, projectVersion))
        JsonArray packagistPackages = composerLockJsonObject.get('packages')?.getAsJsonArray()
        DependencyNodeBuilder dependencyNodeBuilder = new DependencyNodeBuilder(rootDependencyNode)

        File composerJsonJsonFile = fileFinder.findFile(projectPath, PackagistBomTool.COMPOSER_JSON)
        JsonObject composerJsonObject = new JsonParser().parse(new JsonReader(new FileReader(composerJsonJsonFile))).getAsJsonObject()

        convertFromJsonToDependencyNode(rootDependencyNode, getStartingPackages(composerJsonObject), packagistPackages, dependencyNodeBuilder)
        rootDependencyNode
    }

    private void convertFromJsonToDependencyNode(DependencyNode parentNode, List<String> currentPackages, JsonArray jsonArray, DependencyNodeBuilder nodeBuilder) {
        if(!currentPackages || !JsonArray) {
            return
        }

        jsonArray.each {
            String currentRowPackageName = it.getAt('name').toString().replace('"', '')

            if(currentPackages.contains(currentRowPackageName)) {
                String currentRowPackageVersion = it.getAt('version')

                DependencyNode newNode = new DependencyNode(currentRowPackageName, currentRowPackageVersion, new NameVersionExternalId(packagistForge, currentRowPackageName, currentRowPackageVersion))

                convertFromJsonToDependencyNode(newNode, getStartingPackages(it.getAsJsonObject()), jsonArray, nodeBuilder)
                nodeBuilder.addChildNodeWithParents(newNode, [parentNode])
            }
        }
    }

    private List<String> getStartingPackages(JsonObject jsonFile) {
        List<String> startingPackages = []

        def requiredPackages = jsonFile.get('require')?.getAsJsonObject()
        requiredPackages?.entrySet().each {
            if(!it.key.equalsIgnoreCase('php')) {
                startingPackages.add(it.key)
            }
        }

        startingPackages
    }
}
