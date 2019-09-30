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
import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeNode;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BitbakeGraphTransformer {
    private static final String NATIVE_SUFFIX = "-native";

    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final ExternalIdFactory externalIdFactory;

    public BitbakeGraphTransformer(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(final BitbakeGraph bitbakeGraph, final Map<String, BitbakeRecipe> componentLayerMap, final Map<String, Integer> layerPriorityMap) {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        final Map<String, Dependency> namesToExternalIds = new HashMap<>();

        for (final BitbakeNode bitbakeNode : bitbakeGraph.getNodes()) {
            final String name = bitbakeNode.getName();

            if (bitbakeNode.getVersion().isPresent()) {
                final String version = bitbakeNode.getVersion().get();
                final Optional<Dependency> dependency = generateExternalId(name, version, componentLayerMap, layerPriorityMap).map(Dependency::new);

                dependency.ifPresent(value -> namesToExternalIds.put(bitbakeNode.getName(), value));
            } else if (name.startsWith("virtual/")) {
                logger.debug(String.format("Virtual component '%s' found. Excluding from graph.", name));
            } else {
                logger.debug(String.format("No version found for component '%s'. It is likely not a real component.", name));
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

    private Optional<ExternalId> generateExternalId(final String name, final String version, final Map<String, BitbakeRecipe> componentLayerMap, final Map<String, Integer> layerPriorityMap) {
        final BitbakeRecipe bitbakeRecipe = componentLayerMap.get(name);
        ExternalId externalId = null;

        if (bitbakeRecipe == null) {
            if (componentLayerMap.containsKey(name)) {
                logger.warn(String.format("Component '%s==%s' is in the component layer map. But potential layers were not populated. This should be reported.", name, version));
            } else {
                logger.debug(String.format("Failed to find component '%s' in component layer map.", name));
            }
            if (name.endsWith(NATIVE_SUFFIX)) {
                final String alternativeName = name.replace(NATIVE_SUFFIX, "");
                logger.debug(String.format("Generating alternative component name '%s' for '%s==%s'", alternativeName, name, version));
                externalId = generateExternalId(alternativeName, version, componentLayerMap, layerPriorityMap).orElse(null);
            } else {
                logger.debug(String.format("'%s==%s' is not an actual component. Excluding from graph.", name, version));
            }
        } else {
            final Optional<BitbakeRecipe.Layer> highestPriorityLayer = getHighestPriorityLayer(bitbakeRecipe, layerPriorityMap);
            externalId = highestPriorityLayer.map(layer -> externalIdFactory.createYoctoExternalId(layer.getLayerName(), name, version)).orElse(null);
        }

        return Optional.ofNullable(externalId);
    }

    private Optional<BitbakeRecipe.Layer> getHighestPriorityLayer(final BitbakeRecipe layerNames, final Map<String, Integer> layerPriorityMap) {
        BitbakeRecipe.Layer priorityLayer = null;
        Integer layerPriority = null;
        for (final BitbakeRecipe.Layer layer : layerNames.getLayers()) {
            final Integer priority = layerPriorityMap.get(layer.getLayerName());
            if (layerPriority == null || priority > layerPriority) {
                priorityLayer = layer;
                layerPriority = priority;
            }
        }

        return Optional.ofNullable(priorityLayer);
    }
}
