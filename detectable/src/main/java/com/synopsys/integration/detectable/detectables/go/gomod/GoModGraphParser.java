/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class GoModGraphParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public GoModGraphParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    DependencyGraph parseGoModGraph(final List<String> goModGraph, final String rootModule) {
        final MutableDependencyGraph mutableDependencyGraph = new MutableMapDependencyGraph();

        for (final String line : goModGraph) {
            //example: github.com/gomods/athens cloud.google.com/go@v0.26.0
            final String[] parts = line.split(" ");
            if (parts.length != 2) {
                logger.warn("Unknown graph line format: " + line);
            } else {
                final Dependency to = parseDependency(parts[1]);
                if (rootModule.equals(parts[0])) {
                    mutableDependencyGraph.addChildToRoot(to);
                } else {
                    final Dependency from = parseDependency(parts[0]);
                    mutableDependencyGraph.addChildWithParent(to, from);
                }
            }
        }

        return mutableDependencyGraph;
    }

    private Dependency parseDependency(final String dependencyPart) {
        if (dependencyPart.contains("@")) {
            final String[] parts = dependencyPart.split("@");
            if (parts.length != 2) {
                logger.warn("Unknown graph dependency format, using entire line as name: " + dependencyPart);
                return new Dependency(dependencyPart, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, dependencyPart, null));
            } else {
                final String name = parts[0];
                String version = parts[1];
                if (version.contains("-")) { //The KB only supports the git hash, unfortunately we must strip out the rest. This gets just the commit has from a go.mod psuedo version.
                    final String[] versionPieces = version.split("-");
                    version = versionPieces[versionPieces.length - 1];
                }
                return new Dependency(name, version, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, name, version));
            }
        } else {
            return new Dependency(dependencyPart, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, dependencyPart, null));
        }
    }

    public List<CodeLocation> parseListAndGoModGraph(final List<String> listOutput, final List<String> modGraphOutput) {
        final List<CodeLocation> codeLocations = new ArrayList<>();
        for (final String module : listOutput) {
            final DependencyGraph graph = parseGoModGraph(modGraphOutput, module);
            codeLocations.add(new CodeLocation(graph, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, module, null)));
        }
        return codeLocations;
    }
}
