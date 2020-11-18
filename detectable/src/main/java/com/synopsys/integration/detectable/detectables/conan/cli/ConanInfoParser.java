/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanGraphNode;
import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanNode;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class ConanInfoParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanInfoNodeParser conanInfoNodeParser;

    public ConanInfoParser(ConanInfoNodeParser conanInfoNodeParser) {
        this.conanInfoNodeParser = conanInfoNodeParser;
    }

    public ConanParseResult generateCodeLocation(String conanInfoOutput) throws IntegrationException {
        Map<String, ConanNode> nodes = generateGraphNodes(conanInfoOutput);
        Optional<ConanNode> rootNode = getRoot(nodes.values());
        if (!rootNode.isPresent()) {
            throw new IntegrationException("No root node found in 'conan info' output");
        }
        ConanGraphNode rootGraphNode = new ConanGraphNode(rootNode.get());
        populateGraphUnderNode(rootGraphNode, nodes);

        NameVersion projectNameVersion = deriveProjectNameVersion(nodes.values());
        List<Dependency> dependencies = generateBdioDependencies(nodes.values());
        CodeLocation codeLocation = generateCodeLocation(dependencies);
        return new ConanParseResult(projectNameVersion.getName(), projectNameVersion.getVersion(), codeLocation);
    }

    private void populateGraphUnderNode(ConanGraphNode curGraphNode, Map<String, ConanNode> graphNodes) throws IntegrationException {
        // TODO only doing requires, not build requires, for now
        for (String childRef : curGraphNode.getNode().getRequiresRefs()) {
            ConanNode childNode = graphNodes.get(childRef);
            if (childNode == null) {
                throw new IntegrationException(String.format("%s requires non-existent node %s", curGraphNode.getNode().getRef(), childRef));
            }
            ConanGraphNode childGraphNode = new ConanGraphNode(childNode);
            populateGraphUnderNode(childGraphNode, graphNodes);
            curGraphNode.addChild(childGraphNode);
        }
    }

    @NotNull
    private CodeLocation generateCodeLocation(List<Dependency> dependencies) {
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        dependencyGraph.addChildrenToRoot(dependencies);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        return codeLocation;
    }

    @NotNull
    private List<Dependency> generateBdioDependencies(Collection<ConanNode> graphNodes) {
        // TODO eventually should use ExternalIdFactory; doubt it can handle these IDs
        //ExternalIdFactory f;
        // The KB supports two forms:
        // <name>/<version>@<user>/<channel>#<recipe_revision>
        // <name>/<version>@<user>/<channel>#<recipe_revision>:<package_id>#<package_revision>
        List<Dependency> dependencies = new ArrayList<>();
        for (ConanNode node : graphNodes) {
            if (!node.isRootNode()) {
                ExternalId externalId = new ExternalId(new Forge("/", "conan"));
                externalId.setName(node.getName());
                externalId.setVersion(generateExternalIdVersionString(node));
                Dependency dep = new Dependency(node.getName(), node.getVersion(), externalId);
                dependencies.add(dep);
            }
        }
        return dependencies;
    }

    @NotNull
    private NameVersion deriveProjectNameVersion(Collection<ConanNode> graphNodes) {
        Optional<ConanNode> rootNode = getRoot(graphNodes);
        String projectName = getStringValue(rootNode, ConanNode::getName).orElse("Unknown");
        String projectVersion = getStringValue(rootNode, ConanNode::getVersion).orElse("Unknown");
        NameVersion projectNameVersion = new NameVersion(projectName, projectVersion);
        return projectNameVersion;
    }

    @NotNull
    private Optional<ConanNode> getRoot(Collection<ConanNode> graphNodes) {
        Optional<ConanNode> rootNode = graphNodes.stream().filter(ConanNode::isRootNode).findFirst();
        return rootNode;
    }

    private String generateExternalIdVersionString(ConanNode node) {
        String externalId;
        if (hasValue(node.getRecipeRevision()) && !hasValue(node.getPackageRevision())) {
            // generate short form
            // <name>/<version>@<user>/<channel>#<recipe_revision>
            externalId = String.format("%s@%s/%s#%s",
                node.getVersion(),
                node.getUser() == null ? "_" : node.getUser(),
                node.getChannel() == null ? "_" : node.getChannel(),
                node.getRecipeRevision());
        } else {
            // generate long form
            // <name>/<version>@<user>/<channel>#<recipe_revision>:<package_id>#<package_revision>
            externalId = String.format("%s@%s/%s#%s:%s#%s",
                node.getVersion(),
                node.getUser() == null ? "_" : node.getUser(),
                node.getChannel() == null ? "_" : node.getChannel(),
                node.getRecipeRevision() == null ? "0" : node.getRecipeRevision(),
                node.getPackageId() == null ? "0" : node.getPackageId(),
                node.getPackageRevision() == null ? "0" : node.getPackageRevision());
        }
        return externalId;
    }

    private boolean hasValue(String value) {
        if ((value == null) || ("None".equals(value))) {
            return false;
        }
        return true;
    }

    // TODO modify ConanGraphNode to return optional?
    private Optional<String> getStringValue(Optional<ConanNode> node, Function<ConanNode, String> stringGetter) {
        if (node.isPresent()) {
            String value = stringGetter.apply(node.get());
            if (value != null) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    private Map<String, ConanNode> generateGraphNodes(String conanInfoOutput) {
        Map<String, ConanNode> graphNodes = new HashMap<>();
        List<String> conanInfoOutputLines = Arrays.asList(conanInfoOutput.split("\n"));
        int lineIndex = 0;
        while (lineIndex < conanInfoOutputLines.size()) {
            String line = conanInfoOutputLines.get(lineIndex);
            logger.debug(String.format("Parsing line: %s", line));
            ConanInfoNodeParseResult nodeParseResult = conanInfoNodeParser.parseNode(conanInfoOutputLines, lineIndex);
            if (nodeParseResult.getConanGraphNode().isPresent()) {
                graphNodes.put(nodeParseResult.getConanGraphNode().get().getRef(), nodeParseResult.getConanGraphNode().get());
            }
            lineIndex = nodeParseResult.getLastParsedLineIndex();
            lineIndex++;
        }
        System.out.printf("Reached end of Conan info output\n");
        return graphNodes;
    }
}
