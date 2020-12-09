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
package com.synopsys.integration.detectable.detectables.conan;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanGraphNode;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;
import com.synopsys.integration.exception.IntegrationException;

public class ConanCodeLocationGenerator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Forge conanForge = new Forge("/", "conan");

    @NotNull
    public ConanDetectableResult generateCodeLocationFromNodeMap(ExternalIdFactory externalIdFactory, boolean includeBuildDependencies, boolean preferLongFormExternalIds, Map<String, ConanNode> nodes) throws IntegrationException {
        logger.debug(String.format("Generating code location from %d dependencies", nodes.keySet().size()));
        Optional<ConanNode> rootNode = getRoot(nodes.values());
        if (!rootNode.isPresent()) {
            throw new IntegrationException("No root node found");
        }
        ConanGraphNode rootGraphNode = new ConanGraphNode(rootNode.get());
        populateGraphUnderNode(rootGraphNode, nodes, includeBuildDependencies);
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        CodeLocation codeLocation = generateCodeLocationFromConanGraph(externalIdFactory, dependencyGraph, rootGraphNode, preferLongFormExternalIds);
        ConanDetectableResult result = new ConanDetectableResult(rootGraphNode.getConanInfoNode().getName(), rootGraphNode.getConanInfoNode().getVersion(), codeLocation);
        return result;
    }

    private void populateGraphUnderNode(ConanGraphNode curGraphNode, Map<String, ConanNode> graphNodes, boolean includeBuildDependencies) throws IntegrationException {
        Set<String> dependencyRefs = new HashSet<>(curGraphNode.getConanInfoNode().getRequiresRefs());
        if (includeBuildDependencies) {
            dependencyRefs.addAll(curGraphNode.getConanInfoNode().getBuildRequiresRefs());
        }
        for (String childRef : dependencyRefs) {
            ConanNode childNode = graphNodes.get(childRef);
            if (childNode == null) {
                throw new IntegrationException(String.format("%s requires non-existent node %s", curGraphNode.getConanInfoNode().getRef(), childRef));
            }
            ConanGraphNode childGraphNode = new ConanGraphNode(childNode);
            populateGraphUnderNode(childGraphNode, graphNodes, includeBuildDependencies);
            curGraphNode.addChild(childGraphNode);
        }
    }

    @NotNull
    private CodeLocation generateCodeLocationFromConanGraph(ExternalIdFactory externalIdFactory, MutableMapDependencyGraph dependencyGraph, ConanGraphNode rootNode,
        boolean preferLongFormExternalIds) {
        addNodeChildrenUnderNode(externalIdFactory, dependencyGraph, 0, rootNode, null, preferLongFormExternalIds);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        return codeLocation;
    }

    private void addNodeChildrenUnderNode(ExternalIdFactory externalIdFactory, MutableMapDependencyGraph dependencyGraph, int depth, ConanGraphNode currentNode, Dependency currentDep,
        boolean preferLongFormExternalIds) {
        Consumer<Dependency> childAdder;
        if (depth == 0) {
            childAdder = childDep -> dependencyGraph.addChildToRoot(childDep);
        } else {
            childAdder = childDep -> dependencyGraph.addChildWithParent(childDep, currentDep);
        }
        for (ConanGraphNode childNode : currentNode.getChildren()) {
            Dependency childDep = generateDependency(externalIdFactory, childNode, preferLongFormExternalIds);
            childAdder.accept(childDep);
            addNodeChildrenUnderNode(externalIdFactory, dependencyGraph, depth + 1, childNode, childDep, preferLongFormExternalIds);
        }
    }

    @NotNull
    private Dependency generateDependency(ExternalIdFactory externalIdFactory, ConanGraphNode graphNode, boolean preferLongFormExternalIds) {
        String depName = graphNode.getConanInfoNode().getName();
        String depVersion = generateExternalIdVersionString(graphNode.getConanInfoNode(), preferLongFormExternalIds);
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(conanForge, depName, depVersion);
        Dependency dep = new Dependency(graphNode.getConanInfoNode().getName(),
            graphNode.getConanInfoNode().getVersion(),
            externalId);
        return dep;
    }

    @NotNull
    private Optional<ConanNode> getRoot(Collection<ConanNode> graphNodes) {
        Optional<ConanNode> rootNode = graphNodes.stream().filter(ConanNode::isRootNode).findFirst();
        return rootNode;
    }

    private String generateExternalIdVersionString(ConanNode node, boolean preferLongFormExternalIds) {
        String externalId;
        if (hasValue(node.getRecipeRevision()) && hasValue(node.getPackageRevision()) && preferLongFormExternalIds) {
            // generate long form
            // <name>/<version>@<user>/<channel>#<recipe_revision>:<package_id>#<package_revision>
            externalId = String.format("%s@%s/%s#%s:%s#%s",
                node.getVersion(),
                node.getUser() == null ? "_" : node.getUser(),
                node.getChannel() == null ? "_" : node.getChannel(),
                node.getRecipeRevision(),
                node.getPackageId() == null ? "0" : node.getPackageId(),
                node.getPackageRevision());
        } else {
            // generate short form
            // <name>/<version>@<user>/<channel>#<recipe_revision>
            externalId = String.format("%s@%s/%s#%s",
                node.getVersion(),
                node.getUser() == null ? "_" : node.getUser(),
                node.getChannel() == null ? "_" : node.getChannel(),
                node.getRecipeRevision() == null ? "0" : node.getRecipeRevision());
        }
        return externalId;
    }

    private boolean hasValue(String value) {
        if ((value == null) || ("None".equals(value))) {
            return false;
        }
        return true;
    }

}
