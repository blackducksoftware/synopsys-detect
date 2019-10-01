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

    public DependencyGraph transform(final BitbakeGraph bitbakeGraph, final Map<String, BitbakeRecipe> componentLayerMap) {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        final Map<String, Dependency> namesToExternalIds = new HashMap<>();

        for (final BitbakeNode bitbakeNode : bitbakeGraph.getNodes()) {
            final String name = bitbakeNode.getName();

            if (bitbakeNode.getVersion().isPresent()) {
                final String version = bitbakeNode.getVersion().get();
                final Optional<Dependency> dependency = generateExternalId(name, version, componentLayerMap).map(Dependency::new);

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

    private Optional<ExternalId> generateExternalId(final String dependencyName, final String dependencyVersion, final Map<String, BitbakeRecipe> componentLayerMap) {
        final BitbakeRecipe bitbakeRecipe = componentLayerMap.get(dependencyName);
        ExternalId externalId = null;

        if (bitbakeRecipe == null) {
            if (componentLayerMap.containsKey(dependencyName)) {
                logger.warn(String.format("Component '%s==%s' is in the component layer map, but layers were not populated. This should be reported.", dependencyName, dependencyVersion));
            } else {
                logger.debug(String.format("Failed to find component '%s' in component layer map.", dependencyName));
            }
            if (dependencyName.endsWith(NATIVE_SUFFIX)) {
                final String alternativeName = dependencyName.replace(NATIVE_SUFFIX, "");
                logger.debug(String.format("Generating alternative component name '%s' for '%s==%s'", alternativeName, dependencyName, dependencyVersion));
                externalId = generateExternalId(alternativeName, dependencyVersion, componentLayerMap).orElse(null);
            } else {
                logger.debug(String.format("'%s==%s' is not an actual component. Excluding from graph.", dependencyName, dependencyVersion));
            }
        } else if (bitbakeRecipe.getLayers().isEmpty()) {
            logger.warn("Component '%s==%s' has a recipe, but could not find any layers. This should be reported.");
        } else {
            // The layer priority can be determined by the order they appear from the "bitbake-layers show-recipes" command. The highest priority layer being first.
            final BitbakeRecipe.Layer highestPriorityLayer = bitbakeRecipe.getLayers().get(0);
            externalId = externalIdFactory.createYoctoExternalId(highestPriorityLayer.getLayerName(), dependencyName, dependencyVersion);
        }

        return Optional.ofNullable(externalId);
    }
}
