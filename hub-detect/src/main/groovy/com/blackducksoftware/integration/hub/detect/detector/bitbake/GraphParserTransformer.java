/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.detector.bitbake;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class GraphParserTransformer {
    public DependencyGraph transform(final GraphParser graphParser, final String targetArchitecture) {
        final Map<String, GraphNode> nodes = graphParser.getNodes();
        final Map<String, GraphEdge> edges = graphParser.getEdges();
        final LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();

        for (final GraphNode graphNode : nodes.values()) {
            final String name = getNameFromNode(graphNode);
            final DependencyId dependencyId = new NameDependencyId(name);
            final Optional<String> version = getVersionFromNode(graphNode);

            if (version.isPresent()) {
                final ExternalId externalId = new ExternalId(BitbakeDetector.YOCTO_FORGE);
                externalId.name = name;
                externalId.version = version.get();
                externalId.architecture = targetArchitecture;
                graphBuilder.setDependencyInfo(dependencyId, name, version.get(), externalId);
            }

            graphBuilder.addChildToRoot(dependencyId);
        }

        for (final GraphEdge graphEdge : edges.values()) {
            final DependencyId node1 = new NameDependencyId(getNameFromNode(graphEdge.getNode1()));
            final DependencyId node2 = new NameDependencyId(getNameFromNode(graphEdge.getNode2()));
            graphBuilder.addParentWithChild(node1, node2);
        }

        return graphBuilder.build();
    }

    private String getNameFromNode(final GraphNode graphNode) {
        return graphNode.getId().replaceAll("\"", "");
    }

    private Optional<String> getVersionFromNode(final GraphNode graphNode) {
        final Optional<String> attribute = getLabelAttribute(graphNode);

        return attribute.map(this::getVersionFromLabel);
    }

    private Optional<String> getLabelAttribute(final GraphNode graphNode) {
        final String attribute = (String) graphNode.getAttribute("label");
        Optional<String> result = Optional.empty();

        if (StringUtils.isNotBlank(attribute)) {
            result = Optional.of(attribute);
        }

        return result;
    }

    private String getVersionFromLabel(final String label) {
        final String[] components = label.split("\\\\n:|\\\\n");
        final String version = components[1];

        return version;
    }
}
