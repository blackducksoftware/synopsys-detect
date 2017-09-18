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

import com.blackducksoftware.integration.hub.bdio.simple.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.simple.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.simple.model.Dependency
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import groovy.transform.TypeChecked

@Component
@TypeChecked
class PackagistParser {

    @Autowired
    DetectConfiguration detectConfiguration

    public DetectCodeLocation getDependencyGraphFromProject(String sourcePath, String composerJsonText, String composerLockText) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        JsonObject composerJsonObject = new JsonParser().parse(composerJsonText) as JsonObject
        String projectName = composerJsonObject.get('name')?.getAsString()
        String projectVersion = composerJsonObject.get('version')?.getAsString()

        JsonObject composerLockObject = new JsonParser().parse(composerLockText) as JsonObject
        JsonArray packagistPackages = composerLockObject.get('packages')?.getAsJsonArray()
        List<String> startingPackages = getStartingPackages(composerJsonObject, false)

        if (detectConfiguration.getPackagistIncludeDevDependencies()) {
            JsonArray packagistDevPackages = composerLockObject.get('packages-dev')?.getAsJsonArray()
            packagistPackages.addAll(packagistDevPackages)
            List<String> startingDevPackages = getStartingPackages(composerJsonObject, true)
            startingPackages.addAll(startingDevPackages)
        }
        convertFromJsonToDependency(graph, null, startingPackages, packagistPackages, true)

        def eid = new NameVersionExternalId(Forge.PACKAGIST, projectName, projectVersion);
        new DetectCodeLocation(BomToolType.PACKAGIST, sourcePath,projectName, projectVersion, eid, graph)
    }

    private void convertFromJsonToDependency(MutableDependencyGraph graph, Dependency parent, List<String> currentPackages, JsonArray jsonArray, Boolean root) {
        if (!currentPackages) {
            return
        }

        jsonArray.each {
            String currentRowPackageName = it.getAt('name').toString().replace('"', '')

            if (currentPackages.contains(currentRowPackageName)) {
                String currentRowPackageVersion = it.getAt('version').toString().replace('"', '')

                Dependency child = new Dependency(currentRowPackageName, currentRowPackageVersion, new NameVersionExternalId(Forge.PACKAGIST, currentRowPackageName, currentRowPackageVersion))

                convertFromJsonToDependency(graph, child, getStartingPackages(it.getAsJsonObject(), false), jsonArray, false)
                if (root){
                    graph.addChildToRoot(child)
                }else{
                    graph.addParentWithChild(parent, child)
                }
            }
        }
    }

    private List<String> getStartingPackages(JsonObject jsonFile, boolean checkDev) {
        List<String> allRequires = []
        def requiredPackages

        if (checkDev) {
            requiredPackages = jsonFile.get('require-dev')?.getAsJsonObject()
        } else {
            requiredPackages = jsonFile.get('require')?.getAsJsonObject()
        }

        requiredPackages?.entrySet().each {
            if (!it.key.equalsIgnoreCase('php')) {
                allRequires.add(it.key)
            }
        }

        allRequires
    }
}
