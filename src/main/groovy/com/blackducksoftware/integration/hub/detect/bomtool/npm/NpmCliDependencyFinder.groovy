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

import java.util.Map.Entry

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NpmCliDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(NpmCliDependencyFinder.class)

    private static final String JSON_NAME = 'name'
    private static final String JSON_VERSION = 'version'
    private static final String JSON_DEPENDENCIES = 'dependencies'

    public DependencyNode generateDependencyNode(File npmLsOutputFile) {
        if (npmLsOutputFile?.length() > 0) {
            logger.info("Generating results from npm ls -json")
            return convertNpmJsonFileToDependencyNode(npmLsOutputFile.text)
        } else {
            logger.error("Ran into an issue creating and writing to file")
        }

        null
    }

    private DependencyNode convertNpmJsonFileToDependencyNode(String npmLsOutput) {
        JsonObject npmJson = new JsonParser().parse(npmLsOutput) as JsonObject

        String projectName = npmJson.getAsJsonPrimitive(JSON_NAME)?.getAsString()
        String projectVersion = npmJson.getAsJsonPrimitive(JSON_VERSION)?.getAsString()

        def externalId = new NameVersionExternalId(Forge.NPM, projectName, projectVersion)
        def dependencyNode = new DependencyNode(projectName, projectVersion, externalId)

        DependencyNodeBuilder dependencyNodeBuilder = new DependencyNodeBuilder(dependencyNode)
        populateChildren(dependencyNodeBuilder, dependencyNode, npmJson.getAsJsonObject(JSON_DEPENDENCIES))

        dependencyNode
    }

    private void populateChildren(DependencyNodeBuilder dependencyNodeBuilder, DependencyNode parentDependencyNode, JsonObject parentNodeChildren) {
        Set<Entry<String, JsonElement>> elements = parentNodeChildren?.entrySet()
        elements?.each { Entry<String, JsonElement> it ->
            JsonObject element = it.value as JsonObject
            String name = it.key
            String version = element.getAsJsonPrimitive(JSON_VERSION)?.getAsString()
            JsonObject children = element.getAsJsonObject(JSON_DEPENDENCIES)

            def externalId = new NameVersionExternalId(Forge.NPM, name, version)
            def newNode = new DependencyNode(name, version, externalId)

            populateChildren(dependencyNodeBuilder, newNode, children)
            dependencyNodeBuilder.addChildNodeWithParents(newNode, [parentDependencyNode])
        }
    }
}
