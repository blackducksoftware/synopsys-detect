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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
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
import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanInfoNode;
import com.synopsys.integration.exception.IntegrationException;

public class ConanInfoParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanInfoNodeParser conanInfoNodeParser;

    public ConanInfoParser(ConanInfoNodeParser conanInfoNodeParser) {
        this.conanInfoNodeParser = conanInfoNodeParser;
    }

    public ConanParseResult generateCodeLocation(String conanInfoOutput) throws IntegrationException {
        Map<String, ConanInfoNode> nodes = generateGraphNodes(conanInfoOutput);
        Optional<ConanInfoNode> rootNode = getRoot(nodes.values());
        if (!rootNode.isPresent()) {
            throw new IntegrationException("No root node found in 'conan info' output");
        }
        ConanGraphNode rootGraphNode = new ConanGraphNode(rootNode.get());
        populateGraphUnderNode(rootGraphNode, nodes);
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        CodeLocation codeLocation = generateCodeLocation(dependencyGraph, rootGraphNode);
        return new ConanParseResult(rootGraphNode.getConanInfoNode().getName(), rootGraphNode.getConanInfoNode().getVersion(), codeLocation);
    }

    private void populateGraphUnderNode(ConanGraphNode curGraphNode, Map<String, ConanInfoNode> graphNodes) throws IntegrationException {
        // TODO only doing requires, not build requires, for now
        for (String childRef : curGraphNode.getConanInfoNode().getRequiresRefs()) {
            ConanInfoNode childNode = graphNodes.get(childRef);
            if (childNode == null) {
                throw new IntegrationException(String.format("%s requires non-existent node %s", curGraphNode.getConanInfoNode().getRef(), childRef));
            }
            ConanGraphNode childGraphNode = new ConanGraphNode(childNode);
            populateGraphUnderNode(childGraphNode, graphNodes);
            curGraphNode.addChild(childGraphNode);
        }
    }

    //    @NotNull
    //    private CodeLocation generateCodeLocation(List<Dependency> dependencies) {
    //        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
    //        dependencyGraph.addChildrenToRoot(dependencies);
    //        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
    //        return codeLocation;
    //    }

    @NotNull
    private CodeLocation generateCodeLocation(MutableMapDependencyGraph dependencyGraph, ConanGraphNode rootNode) {
        addNodeChildrenUnderNode(dependencyGraph, 0, rootNode, null);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        return codeLocation;
    }

    private void addNodeChildrenUnderNode(MutableMapDependencyGraph dependencyGraph, int depth, ConanGraphNode currentNode, Dependency currentDep) {
        Consumer<Dependency> childAdder;
        if (depth == 0) {
            childAdder = childDep -> dependencyGraph.addChildToRoot(childDep);
        } else {
            childAdder = childDep -> dependencyGraph.addChildWithParent(childDep, currentDep);
        }
        for (ConanGraphNode childNode : currentNode.getChildren()) {
            Dependency childDep = generateDependency(childNode);
            childAdder.accept(childDep);
            addNodeChildrenUnderNode(dependencyGraph, depth + 1, childNode, childDep);
        }
    }

    @NotNull
    private Dependency generateDependency(ConanGraphNode graphNode) {
        // TODO eventually should use ExternalIdFactory; doubt it can handle these IDs
        //ExternalIdFactory f;
        // The KB supports two forms:
        // <name>/<version>@<user>/<channel>#<recipe_revision>
        // <name>/<version>@<user>/<channel>#<recipe_revision>:<package_id>#<package_revision>
        // TODO generate forge once
        ExternalId externalId = new ExternalId(new Forge("/", "conan"));
        externalId.setName(graphNode.getConanInfoNode().getName());
        externalId.setVersion(generateExternalIdVersionString(graphNode.getConanInfoNode()));
        Dependency dep = new Dependency(graphNode.getConanInfoNode().getName(), graphNode.getConanInfoNode().getVersion(), externalId);
        return dep;
    }

    @NotNull
    private Optional<ConanInfoNode> getRoot(Collection<ConanInfoNode> graphNodes) {
        Optional<ConanInfoNode> rootNode = graphNodes.stream().filter(ConanInfoNode::isRootNode).findFirst();
        return rootNode;
    }

    private String generateExternalIdVersionString(ConanInfoNode node) {
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
    private Optional<String> getStringValue(Optional<ConanInfoNode> node, Function<ConanInfoNode, String> stringGetter) {
        if (node.isPresent()) {
            String value = stringGetter.apply(node.get());
            if (value != null) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    private Map<String, ConanInfoNode> generateGraphNodes(String conanInfoOutput) {
        Map<String, ConanInfoNode> graphNodes = new HashMap<>();
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
