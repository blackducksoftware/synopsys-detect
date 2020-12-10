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
package com.synopsys.integration.detectable.detectables.conan.lockfile.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.ConanExternalIdVersionGenerator;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model.ConanLockfileData;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model.ConanLockfileNode;
import com.synopsys.integration.exception.IntegrationException;

public class ConanLockfileParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanCodeLocationGenerator conanCodeLocationGenerator;
    private final ExternalIdFactory externalIdFactory;
    private final ConanExternalIdVersionGenerator versionGenerator;

    public ConanLockfileParser(ConanCodeLocationGenerator conanCodeLocationGenerator, ExternalIdFactory externalIdFactory, ConanExternalIdVersionGenerator versionGenerator) {
        this.conanCodeLocationGenerator = conanCodeLocationGenerator;
        this.externalIdFactory = externalIdFactory;
        this.versionGenerator = versionGenerator;
    }

    public ConanDetectableResult generateCodeLocationFromConanLockfileContents(Gson gson, String conanLockfileContents,
        boolean includeBuildDependencies, boolean preferLongFormExternalIds) throws IntegrationException {
        logger.trace(String.format("Parsing conan lockfile contents:\n%s", conanLockfileContents));
        Map<Integer, ConanNode> indexedNodeMap = generateIndexedNodeMap(gson, conanLockfileContents);
        // The lockfile references nodes by (integer) index; generator needs nodes referenced by names (component references)
        Map<String, ConanNode> namedNodeMap = convertToNamedNodeMap(indexedNodeMap);
        ConanDetectableResult result = conanCodeLocationGenerator.generateCodeLocationFromNodeMap(externalIdFactory, versionGenerator, includeBuildDependencies, preferLongFormExternalIds, namedNodeMap);
        return result;
    }

    private Map<Integer, ConanNode> generateIndexedNodeMap(Gson gson, String conanLockfileContents) {
        Map<Integer, ConanNode> graphNodes = new HashMap<>();
        ConanLockfileData conanLockfileData = gson.fromJson(conanLockfileContents, ConanLockfileData.class);
        logger.trace(String.format("conanLockfileData: %s", conanLockfileData));
        if (!conanLockfileData.getConanLockfileGraph().isRevisionsEnabled()) {
            logger.warn("The Conan revisions feature is not enabled, which will significantly reduce Black Duck's ability to identify dependencies");
        } else {
            logger.trace("The Conan revisions feature is enabled");
        }
        for (Map.Entry<Integer, ConanLockfileNode> entry : conanLockfileData.getConanLockfileGraph().getNodeMap().entrySet()) {
            logger.trace(String.format("%d: %s:%s#%s", entry.getKey(), entry.getValue().getRef().orElse("?"), entry.getValue().getPackageId().orElse("?"), entry.getValue().getPackageRevision().orElse("?")));
            ConanLockfileNode lockfileNode = entry.getValue();
            Optional<ConanNode> conanNode = generateConanNode(entry.getKey(), lockfileNode);
            conanNode.ifPresent(node -> graphNodes.put(entry.getKey(), node));
        }
        logger.trace(String.format("ConanNode map: %s", graphNodes));
        return graphNodes;
    }

    private Optional<ConanNode> generateConanNode(Integer nodeKey, ConanLockfileNode lockfileNode) {
        ConanNodeBuilder nodeBuilder = new ConanNodeBuilder();
        if (nodeKey == 0) {
            nodeBuilder.forceRootNode();
        }
        nodeBuilder.setRefFromLockfile(lockfileNode.getRef().orElse(null));
        nodeBuilder.setPath(lockfileNode.getPath().orElse(null));
        lockfileNode.getPackageId().ifPresent(pkgId -> nodeBuilder.setPackageId(pkgId));
        lockfileNode.getPackageRevision().ifPresent(pkgRev -> nodeBuilder.setPackageRevision(pkgRev));
        nodeBuilder.setRequiresIndices(lockfileNode.getRequires());
        nodeBuilder.setBuildRequiresIndices(lockfileNode.getBuildRequires());
        Optional<ConanNode> conanNode = nodeBuilder.build();
        return conanNode;
    }

    private Map<String, ConanNode> convertToNamedNodeMap(Map<Integer, ConanNode> numberedNodeMap) {
        Map<String, ConanNode> nodeMap = new HashMap<>(numberedNodeMap.size());
        for (Integer index : numberedNodeMap.keySet()) {
            ConanNode node = numberedNodeMap.get(index);
            addRefsForGivenIndices(numberedNodeMap, node.getRequiresIndices(), ref -> node.addRequiresRef(ref));
            addRefsForGivenIndices(numberedNodeMap, node.getBuildRequiresIndices(), ref -> node.addBuildRequiresRef(ref));
            nodeMap.put(node.getRef(), node);
        }
        return nodeMap;
    }

    // Translate each of the given map indices to the corresponding dependency ref,
    // and call the given refAdder to put it where it belongs
    private void addRefsForGivenIndices(Map<Integer, ConanNode> numberedNodeMap, List<Integer> indices, Consumer<String> refAdder) {
        indices.stream()
            .map(index -> numberedNodeMap.get(index).getRef())
            .forEach(ref -> refAdder.accept(ref));
    }
}
