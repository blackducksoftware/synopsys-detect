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

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
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

    public ExternalIdFactory externalIdFactory;
    public NpmCliDependencyFinder(ExternalIdFactory externalIdFactory){
        this.externalIdFactory = externalIdFactory;
    }

    public DetectCodeLocation generateCodeLocation(String sourcePath, File npmLsOutputFile) {
        if (npmLsOutputFile?.length() > 0) {
            logger.info("Generating results from npm ls -json")
            return convertNpmJsonFileToCodeLocation(sourcePath, npmLsOutputFile.text)
        } else {
            logger.error("Ran into an issue creating and writing to file")
        }

        null
    }
    private DetectCodeLocation convertNpmJsonFileToCodeLocation(String sourcePath, String npmLsOutput) {
        JsonObject npmJson = new JsonParser().parse(npmLsOutput) as JsonObject
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        String projectName = npmJson.getAsJsonPrimitive(JSON_NAME)?.getAsString()
        String projectVersion = npmJson.getAsJsonPrimitive(JSON_VERSION)?.getAsString()

        populateChildren(graph, null, npmJson.getAsJsonObject(JSON_DEPENDENCIES), true)

        def externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, projectName, projectVersion)

        new DetectCodeLocation(BomToolType.NPM, sourcePath, projectName, projectVersion, externalId, graph)
    }
    private void populateChildren(MutableDependencyGraph graph, Dependency parentDependency, JsonObject parentNodeChildren, Boolean root) {
        Set<Entry<String, JsonElement>> elements = parentNodeChildren?.entrySet()
        elements?.each { Entry<String, JsonElement> it ->
            JsonObject element = it.value as JsonObject
            String name = it.key
            String version = element.getAsJsonPrimitive(JSON_VERSION)?.getAsString()
            JsonObject children = element.getAsJsonObject(JSON_DEPENDENCIES)

            def externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, name, version)
            def child = new Dependency(name, version, externalId)

            populateChildren(graph, child, children, false)
            if (root){
                graph.addChildToRoot(child)
            }else{
                graph.addParentWithChild(parentDependency, child)
            }
        }
    }
}
