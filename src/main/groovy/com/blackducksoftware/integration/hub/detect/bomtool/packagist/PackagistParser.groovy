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
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.FileFinder
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader

@Component
class PackagistParser {
    private Forge packagistForge = new Forge('packagist', ':')

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    public DependencyNode getDependencyNodeFromProject(String projectPath) {
        File composerJsonJsonFile = fileFinder.findFile(projectPath, PackagistBomTool.COMPOSER_JSON)

        JsonObject composerJsonJsonObject = new JsonParser().parse(new JsonReader(new FileReader(composerJsonJsonFile))).getAsJsonObject()
        String projectName = projectInfoGatherer.getProjectName(BomToolType.PACKAGIST, projectPath, composerJsonJsonObject.get('name')?.getAsString())
        String projectVersion = projectInfoGatherer.getProjectVersionName(composerJsonJsonObject.get('version')?.getAsString())

        def rootDependencyNode = new DependencyNode(projectName, projectVersion, new NameVersionExternalId(packagistForge, projectName, projectVersion))
        DependencyNodeBuilder dependencyNodeBuilder = new DependencyNodeBuilder(rootDependencyNode)

        File composerLockJsonFile = fileFinder.findFile(projectPath, PackagistBomTool.COMPOSER_LOCK)
        JsonObject composerLockJsonObject = new JsonParser().parse(new JsonReader(new FileReader(composerLockJsonFile))).getAsJsonObject()

        JsonArray packagistPackages = composerLockJsonObject.get('packages')?.getAsJsonArray()
        JsonArray packagistDevPackages = composerLockJsonObject.get('packages-dev')?.getAsJsonArray()

        convertFromJsonToDependencyNode(rootDependencyNode, getStartingPackages(composerJsonJsonObject, false), packagistPackages, dependencyNodeBuilder)
        convertFromJsonToDependencyNode(rootDependencyNode, getStartingPackages(composerJsonJsonObject, true), packagistDevPackages, dependencyNodeBuilder)
        rootDependencyNode
    }

    private void convertFromJsonToDependencyNode(DependencyNode parentNode, List<String> currentPackages, JsonArray jsonArray, DependencyNodeBuilder nodeBuilder) {
        if(!currentPackages) {
            return
        }

        jsonArray.each {
            String currentRowPackageName = it.getAt('name').toString().replace('"', '')

            if(currentPackages.contains(currentRowPackageName)) {
                String currentRowPackageVersion = it.getAt('version').toString().replace('"', '')

                DependencyNode newNode = new DependencyNode(currentRowPackageName, currentRowPackageVersion, new NameVersionExternalId(packagistForge, currentRowPackageName, currentRowPackageVersion))

                convertFromJsonToDependencyNode(newNode, getStartingPackages(it.getAsJsonObject(), false), jsonArray, nodeBuilder)
                nodeBuilder.addChildNodeWithParents(newNode, [parentNode])
            }
        }
    }

    private List<String> getStartingPackages(JsonObject jsonFile, boolean checkDev) {
        List<String> allRequires = []

        def requiredPackages = jsonFile.get('require')?.getAsJsonObject()
        requiredPackages?.entrySet().each {
            if(!it.key.equalsIgnoreCase('php')) {
                allRequires.add(it.key)
            }
        }
        if(checkDev) {
            def devRequiredPackages = jsonFile.get('require-dev')?.getAsJsonObject()
            devRequiredPackages?.entrySet().each {
                allRequires.add(it.key)
            }
        }


        allRequires
    }
}
