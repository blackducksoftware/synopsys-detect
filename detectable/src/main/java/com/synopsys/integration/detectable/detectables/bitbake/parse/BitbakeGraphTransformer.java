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
package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeNode;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BitbakeGraphTransformer {
    private final String NATIVE_SUFFIX = "-native";
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final ExternalIdFactory externalIdFactory;

    public BitbakeGraphTransformer(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(final BitbakeGraph bitbakeGraph, final Map<String, List<String>> componentLayerMap, final Map<String, Integer> layerPriorityMap) {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        final Map<String, Dependency> namesToExternalIds = new HashMap<>();
        for (final BitbakeNode bitbakeNode : bitbakeGraph.getNodes()) {
            if (bitbakeNode.getVersion().isPresent()) {
                final String name = bitbakeNode.getName();
                final String version = bitbakeNode.getVersion().get();
                final Optional<Dependency> dependency = generateExternalId(name, version, componentLayerMap, layerPriorityMap)
                                                            .map(externalId -> new Dependency(name, version, externalId));

                if (dependency.isPresent()) {
                    namesToExternalIds.put(bitbakeNode.getName(), dependency.get());
                } else {
                    logger.warn("Failed to properly construct external ID for '%s==$s'. It may be missing a layer mapping.");
                }
            }
        }

        for (final BitbakeNode bitbakeNode : bitbakeGraph.getNodes()) {
            final String name = bitbakeNode.getName();
            if (namesToExternalIds.containsKey(name)) {
                final Dependency dependency = namesToExternalIds.get(bitbakeNode.getName());
                dependencyGraph.addChildToRoot(dependency);
                for (final String child : bitbakeNode.getChildren()) {
                    if (namesToExternalIds.containsKey(child)) {
                        final Dependency childDependency = namesToExternalIds.get(child);
                        dependencyGraph.addParentWithChild(dependency, childDependency);
                    }
                }
            }
        }

        return dependencyGraph;
    }

    private Optional<ExternalId> generateExternalId(final String name, final String version, final Map<String, List<String>> componentLayerMap, final Map<String, Integer> layerPriorityMap) {
        final List<String> potentialLayers = componentLayerMap.get(name);
        if (potentialLayers == null || potentialLayers.isEmpty()) {
            if (componentLayerMap.containsKey(name)) {
                logger.debug(String.format("Component '%s' is in the component layer map. But not potential layers were populated.", name));
            } else {
                logger.debug(String.format("Failed to find component '%s' in component layer map.", name));
            }
            if (name.endsWith(NATIVE_SUFFIX)) {
                logger.debug(String.format("Generating alternative component name for '%s==%s'\n", name, version));
                return generateExternalId(name.replace(NATIVE_SUFFIX, ""), version, componentLayerMap, layerPriorityMap);
            } else {
                logger.debug(String.format("Creating legacy external id for component '%s==%s'.\n", name, version));
                return Optional.ofNullable(externalIdFactory.createNameVersionExternalId(Forge.YOCTO, name, version));
            }
        } else {
            final Optional<String> highestPriorityLayer = getHighestPriorityLayer(potentialLayers, layerPriorityMap);
            return highestPriorityLayer.map(layer -> externalIdFactory.createModuleNamesExternalId(Forge.YOCTO, layer, name, version));
        }
    }

    private Optional<String> getHighestPriorityLayer(final List<String> layerNames, final Map<String, Integer> layerPriorityMap) {
        String layerName = null;
        Integer layerPriority = null;
        for (final String layer : layerNames) {
            final Integer priority = layerPriorityMap.get(layer);
            if (layerPriority == null || priority > layerPriority) {
                layerName = layer;
                layerPriority = priority;
            }
        }

        return Optional.ofNullable(layerName);
    }
}
