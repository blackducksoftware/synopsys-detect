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
import java.util.StringTokenizer;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
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
    private final Gson gson;
    private final ConanCodeLocationGenerator conanCodeLocationGenerator;
    private final ExternalIdFactory externalIdFactory;
    private final ConanExternalIdVersionGenerator versionGenerator;

    public ConanLockfileParser(Gson gson, ConanCodeLocationGenerator conanCodeLocationGenerator, ExternalIdFactory externalIdFactory, ConanExternalIdVersionGenerator versionGenerator) {
        this.gson = gson;
        this.conanCodeLocationGenerator = conanCodeLocationGenerator;
        this.externalIdFactory = externalIdFactory;
        this.versionGenerator = versionGenerator;
    }

    public ConanDetectableResult generateCodeLocationFromConanLockfileContents(String conanLockfileContents,
        boolean includeBuildDependencies, boolean preferLongFormExternalIds) throws IntegrationException {
        logger.trace("Parsing conan lockfile contents:\n{}", conanLockfileContents);
        Map<Integer, ConanNode> indexedNodeMap = generateIndexedNodeMap(conanLockfileContents);
        // The lockfile references nodes by (integer) index; generator needs nodes referenced by names (component references)
        Map<String, ConanNode> namedNodeMap = convertToNamedNodeMap(indexedNodeMap);
        return conanCodeLocationGenerator.generateCodeLocationFromNodeMap(externalIdFactory, versionGenerator, includeBuildDependencies, preferLongFormExternalIds, namedNodeMap);
    }

    private Map<Integer, ConanNode> generateIndexedNodeMap(String conanLockfileContents) {
        Map<Integer, ConanNode> graphNodes = new HashMap<>();
        ConanLockfileData conanLockfileData = gson.fromJson(conanLockfileContents, ConanLockfileData.class);
        logger.trace("conanLockfileData: {}", conanLockfileData);
        if (!conanLockfileData.getConanLockfileGraph().isRevisionsEnabled()) {
            logger.warn("The Conan revisions feature is not enabled, which will significantly reduce Black Duck's ability to identify dependencies");
        } else {
            logger.trace("The Conan revisions feature is enabled");
        }
        for (Map.Entry<Integer, ConanLockfileNode> entry : conanLockfileData.getConanLockfileGraph().getNodeMap().entrySet()) {
            logger.trace("{}: {}:{}#{}", entry.getKey(),
                entry.getValue().getRef().orElse("?"),
                entry.getValue().getPackageId().orElse("?"),
                entry.getValue().getPackageRevision().orElse("?"));
            ConanLockfileNode lockfileNode = entry.getValue();
            Optional<ConanNode> conanNode = generateConanNode(entry.getKey(), lockfileNode);
            conanNode.ifPresent(node -> graphNodes.put(entry.getKey(), node));
        }
        logger.trace("ConanNode map: {}", graphNodes);
        return graphNodes;
    }

    private Optional<ConanNode> generateConanNode(Integer nodeKey, ConanLockfileNode lockfileNode) {
        ConanNodeBuilder nodeBuilder = new ConanNodeBuilder();
        if (nodeKey == 0) {
            nodeBuilder.forceRootNode();
        }
        setRefAndDerivedFields(nodeBuilder, lockfileNode.getRef().orElse(null));
        nodeBuilder.setPath(lockfileNode.getPath().orElse(null));
        lockfileNode.getPackageId().ifPresent(nodeBuilder::setPackageId);
        lockfileNode.getPackageRevision().ifPresent(nodeBuilder::setPackageRevision);
        nodeBuilder.setRequiresIndices(lockfileNode.getRequires());
        nodeBuilder.setBuildRequiresIndices(lockfileNode.getBuildRequires());
        return nodeBuilder.build();
    }

    private Map<String, ConanNode> convertToNamedNodeMap(Map<Integer, ConanNode> numberedNodeMap) {
        Map<String, ConanNode> nodeMap = new HashMap<>(numberedNodeMap.size());
        for (Map.Entry<Integer, ConanNode> entry : numberedNodeMap.entrySet()) {
            ConanNode node = entry.getValue();
            addRefsForGivenIndices(numberedNodeMap, node.getRequiresIndices(), node::addRequiresRef);
            addRefsForGivenIndices(numberedNodeMap, node.getBuildRequiresIndices(), node::addBuildRequiresRef);
            nodeMap.put(node.getRef(), node);
        }
        return nodeMap;
    }

    // Translate each of the given map indices to the corresponding dependency ref,
    // and call the given refAdder to put it where it belongs
    private void addRefsForGivenIndices(Map<Integer, ConanNode> numberedNodeMap, List<Integer> indices, Consumer<String> refAdder) {
        indices.stream()
            .map(index -> numberedNodeMap.get(index).getRef())
            .forEach(refAdder::accept);
    }

    private void setRefAndDerivedFields(ConanNodeBuilder nodeBuilder, String ref) {
        if (StringUtils.isBlank(ref)) {
            return;
        }
        ref = ref.trim();
        StringTokenizer tokenizer = new StringTokenizer(ref, "@/#");
        if (!ref.startsWith("conanfile.")) {
            if (tokenizer.hasMoreTokens()) {
                nodeBuilder.setName(tokenizer.nextToken());
            }
            if (tokenizer.hasMoreTokens()) {
                nodeBuilder.setVersion(tokenizer.nextToken());
            }
            if (ref.contains("@")) {
                nodeBuilder.setUser(tokenizer.nextToken());
                nodeBuilder.setChannel(tokenizer.nextToken());
            }
            if (ref.contains("#")) {
                nodeBuilder.setRecipeRevision(tokenizer.nextToken());
            }
        }
        nodeBuilder.setRef(ref);
    }
}
