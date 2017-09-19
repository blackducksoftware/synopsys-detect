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

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import groovy.transform.TypeChecked

@Component
@TypeChecked
class PackagistParser {

    @Autowired
    DetectConfiguration detectConfiguration

    public DependencyNode getDependencyNodeFromProject(String composerJsonText, String composerLockText) {
        JsonObject composerJsonObject = new JsonParser().parse(composerJsonText) as JsonObject
        String projectName = composerJsonObject.get('name')?.getAsString()
        String projectVersion = composerJsonObject.get('version')?.getAsString()

        def rootDependencyNode = new DependencyNode(projectName, projectVersion, new NameVersionExternalId(Forge.PACKAGIST, projectName, projectVersion))

        JsonObject composerLockObject = new JsonParser().parse(composerLockText) as JsonObject
        JsonArray packagistPackages = composerLockObject.get('packages')?.getAsJsonArray()
        List<String> startingPackages = getStartingPackages(composerJsonObject, false)

        if (detectConfiguration.getPackagistIncludeDevDependencies()) {
            JsonArray packagistDevPackages = composerLockObject.get('packages-dev')?.getAsJsonArray()
            packagistPackages.addAll(packagistDevPackages)
            List<String> startingDevPackages = getStartingPackages(composerJsonObject, true)
            startingPackages.addAll(startingDevPackages)
        }
        convertFromJsonToDependencyNode(rootDependencyNode, startingPackages, packagistPackages)

        rootDependencyNode
    }

    private void convertFromJsonToDependencyNode(DependencyNode parentNode, List<String> currentPackages, JsonArray jsonArray) {
        if (!currentPackages) {
            return
        }

        jsonArray.each {
            String currentRowPackageName = it.getAt('name').toString().replace('"', '')

            if (currentPackages.contains(currentRowPackageName)) {
                String currentRowPackageVersion = it.getAt('version').toString().replace('"', '')

                DependencyNode childNode = new DependencyNode(currentRowPackageName, currentRowPackageVersion, new NameVersionExternalId(Forge.PACKAGIST, currentRowPackageName, currentRowPackageVersion))

                convertFromJsonToDependencyNode(childNode, getStartingPackages(it.getAsJsonObject(), false), jsonArray)
                parentNode.children.add(childNode)
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
