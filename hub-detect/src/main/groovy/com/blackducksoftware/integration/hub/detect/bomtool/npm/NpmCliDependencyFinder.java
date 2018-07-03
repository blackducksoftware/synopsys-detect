/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.npm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Component
public class NpmCliDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(NpmCliDependencyFinder.class);

    private static final String JSON_NAME = "name";
    private static final String JSON_VERSION = "version";
    private static final String JSON_DEPENDENCIES = "dependencies";

    public ExternalIdFactory externalIdFactory;

    NpmCliDependencyFinder(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public NpmParseResult generateCodeLocation(final BomToolType bomToolType, final String sourcePath, final File npmLsOutputFile) throws IOException {
        if (npmLsOutputFile == null || npmLsOutputFile.length() <= 0) {
            logger.error("Ran into an issue creating and writing to file");
            return null;
        }

        logger.info("Generating results from npm ls -json");

        return convertNpmJsonFileToCodeLocation(bomToolType, sourcePath, FileUtils.readFileToString(npmLsOutputFile, StandardCharsets.UTF_8));
    }

    private NpmParseResult convertNpmJsonFileToCodeLocation(final BomToolType bomToolType, final String sourcePath, final String npmLsOutput) {
        final JsonObject npmJson = new JsonParser().parse(npmLsOutput).getAsJsonObject();
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        final JsonElement projectNameElement = npmJson.getAsJsonPrimitive(JSON_NAME);
        final JsonElement projectVersionElement = npmJson.getAsJsonPrimitive(JSON_VERSION);
        String projectName = null;
        String projectVersion = null;
        if (projectNameElement != null) {
            projectName = projectNameElement.getAsString();
        }
        if (projectVersionElement != null) {
            projectVersion = projectVersionElement.getAsString();
        }

        populateChildren(graph, null, npmJson.getAsJsonObject(JSON_DEPENDENCIES), true);

        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, projectName, projectVersion);

        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.NPM, bomToolType, sourcePath, externalId, graph).build();

        return new NpmParseResult(projectName, projectVersion, codeLocation);

    }

    private void populateChildren(final MutableDependencyGraph graph, final Dependency parentDependency, final JsonObject parentNodeChildren, final Boolean root) {
        if (parentNodeChildren == null) {
            return;
        }
        final Set<Entry<String, JsonElement>> elements = parentNodeChildren.entrySet();
        elements.forEach(it -> {
            if (it.getValue() != null && it.getValue().isJsonObject()) {

            }
            final JsonObject element = it.getValue().getAsJsonObject();
            final String name = it.getKey();
            String version = null;
            final JsonPrimitive versionPrimitive = element.getAsJsonPrimitive(JSON_VERSION);
            if (versionPrimitive != null && versionPrimitive.isString()) {
                version = versionPrimitive.getAsString();
            }
            final JsonObject children = element.getAsJsonObject(JSON_DEPENDENCIES);

            if (name != null && version != null) {
                final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, name, version);
                final Dependency child = new Dependency(name, version, externalId);

                populateChildren(graph, child, children, false);
                if (root) {
                    graph.addChildToRoot(child);
                } else {
                    graph.addParentWithChild(parentDependency, child);
                }
            }
        });
    }
}
