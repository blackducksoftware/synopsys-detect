/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.go.gogradle;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.exception.IntegrationException;

public class GoGradleLockParser {
    private final ExternalIdFactory externalIdFactory;

    public GoGradleLockParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parse(final File goGradleLockFile) throws IOException, IntegrationException {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        final YAMLMapper mapper = new YAMLMapper();
        final JsonNode rootNode = mapper.readTree(goGradleLockFile);
        final JsonNode buildNode = rootNode.findPath("build");

        if (buildNode == null) {
            throw new IntegrationException(String.format("Failed to find build node in %s", GoGradleDetectable.GO_GRADLE_LOCK));
        }

        for (final JsonNode dependencyNode : buildNode) {
            final Optional<String> name = Optional.ofNullable(dependencyNode.get("name")).map(JsonNode::textValue);
            final Optional<String> commit = Optional.ofNullable(dependencyNode.get("commit")).map(JsonNode::textValue);

            if (name.isPresent() && commit.isPresent()) {
                String dependencyName = name.get();
                if (dependencyName.startsWith("golang.org/x/")) {
                    dependencyName = dependencyName.replace("golang.org/x/", "");
                }
                final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, dependencyName, commit.get());
                final Dependency dependency = new Dependency(externalId);
                dependencyGraph.addChildToRoot(dependency);
            }
        }

        return dependencyGraph;
    }
}
