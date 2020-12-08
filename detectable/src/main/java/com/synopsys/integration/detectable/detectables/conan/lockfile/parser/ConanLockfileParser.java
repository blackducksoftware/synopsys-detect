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
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model.ConanLockfileData;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model.ConanLockfileNode;
import com.synopsys.integration.exception.IntegrationException;

public class ConanLockfileParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanCodeLocationGenerator conanCodeLocationGenerator;

    public ConanLockfileParser(ConanCodeLocationGenerator conanCodeLocationGenerator) {
        this.conanCodeLocationGenerator = conanCodeLocationGenerator;
    }

    public ConanDetectableResult generateCodeLocationFromConanLockfileContents(Gson gson, String conanLockfileContents, boolean includeBuildDependencies) throws IntegrationException {
        logger.trace(String.format("Parsing conan info output:\n%s", conanLockfileContents));
        Map<Integer, ConanNode> numberedNodeMap = generateNumberedNodeMap(gson, conanLockfileContents);
        Map<String, ConanNode> nodeMap = generateNodeMap(numberedNodeMap);
        // The future lockfile detectable will also generate a nodeMap; once a nodeMap is generated, processing (translation to a codelocation) is identical
        ConanDetectableResult result = conanCodeLocationGenerator.generateCodeLocationFromNodeMap(includeBuildDependencies, nodeMap);
        return result;
        //        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        //        ConanLockfileData conanLockfileData = gson.fromJson(conanLockfileContents, ConanLockfileData.class);
        //        logger.trace(String.format("conanLockfileData: %s", conanLockfileData));
        //        if (!conanLockfileData.getConanLockfileGraph().isRevisionsEnabled()) {
        //            logger.warn("The Conan revisions feature is not enabled, which will significantly reduce Black Duck's ability to identify dependencies");
        //        } else {
        //            logger.trace("The Conan revisions feature is enabled");
        //        }
        //        logger.info(String.format("Node 0 path: %s", conanLockfileData.getConanLockfileGraph().getNodeMap().get(0).getPath().get()));
        //        for (Map.Entry<Integer, ConanLockfileNode> entry : conanLockfileData.getConanLockfileGraph().getNodeMap().entrySet()) {
        //            logger.info(String.format("%d: %s:%s#%s", entry.getKey(), entry.getValue().getRef().orElse("?"), entry.getValue().getPackageId().orElse("?"), entry.getValue().getPackageRevision().orElse("?")));
        //        }
        //        return graph;
    }

    private Map<Integer, ConanNode> generateNumberedNodeMap(Gson gson, String conanLockfileContents) {
        Map<Integer, ConanNode> graphNodes = new HashMap<>();
        ConanLockfileData conanLockfileData = gson.fromJson(conanLockfileContents, ConanLockfileData.class);
        logger.trace(String.format("conanLockfileData: %s", conanLockfileData));
        if (!conanLockfileData.getConanLockfileGraph().isRevisionsEnabled()) {
            logger.warn("The Conan revisions feature is not enabled, which will significantly reduce Black Duck's ability to identify dependencies");
        } else {
            logger.trace("The Conan revisions feature is enabled");
        }
        logger.info(String.format("Node 0 path: %s", conanLockfileData.getConanLockfileGraph().getNodeMap().get(0).getPath().orElse("?")));
        for (Map.Entry<Integer, ConanLockfileNode> entry : conanLockfileData.getConanLockfileGraph().getNodeMap().entrySet()) {
            logger.info(String.format("%d: %s:%s#%s", entry.getKey(), entry.getValue().getRef().orElse("?"), entry.getValue().getPackageId().orElse("?"), entry.getValue().getPackageRevision().orElse("?")));
            ConanLockfileNode lockfileNode = entry.getValue();
            ConanNodeBuilder nodeBuilder = new ConanNodeBuilder();
            if (entry.getKey() == 0) {
                nodeBuilder.forceRootNode();
            }
            // TODO builder should be able to take lockfileNode and build from that!! or maybe it's a separate builder?
            nodeBuilder.setRefFromLockfile(lockfileNode.getRef().orElse(null));
            nodeBuilder.setPath(lockfileNode.getPath().orElse(null));
            if (lockfileNode.getPackageId().isPresent()) {
                nodeBuilder.setPackageId(lockfileNode.getPackageId().get());
            }
            if (lockfileNode.getPackageRevision().isPresent()) {
                nodeBuilder.setPackageRevision(lockfileNode.getPackageRevision().get());
            }
            nodeBuilder.setRequiresIndices(lockfileNode.getRequires());
            nodeBuilder.setBuildRequiresIndices(lockfileNode.getBuildRequires());
            Optional<ConanNode> conanNode = nodeBuilder.build();
            if (conanNode.isPresent()) {
                graphNodes.put(entry.getKey(), conanNode.get());
            }
        }
        logger.info(String.format("ConanNode map: %s", graphNodes));
        logger.trace("Reached end of Conan lockfile data");
        return graphNodes;
    }

    private Map<String, ConanNode> generateNodeMap(Map<Integer, ConanNode> numberedNodeMap) {
        Map<String, ConanNode> nodeMap = new HashMap<>(numberedNodeMap.size());
        for (Map.Entry<Integer, ConanNode> entry : numberedNodeMap.entrySet()) {
            // TODO FILL IN requiresRefs
            for (Integer requiresIndex : entry.getValue().getRequiresIndices()) {
                String requiresRef = numberedNodeMap.get(requiresIndex).getRef();
                entry.getValue().addRequiresRef(requiresRef);
            }
            for (Integer buildRequiresIndex : entry.getValue().getBuildRequiresIndices()) {
                String buildRequiresRef = numberedNodeMap.get(buildRequiresIndex).getRef();
                entry.getValue().addBuildRequiresRef(buildRequiresRef);
            }
            nodeMap.put(entry.getValue().getRef(), entry.getValue());
        }
        return nodeMap;
    }
}
