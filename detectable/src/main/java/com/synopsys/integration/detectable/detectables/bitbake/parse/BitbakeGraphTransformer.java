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

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeNode;

public class BitbakeGraphTransformer {
    private final ExternalIdFactory externalIdFactory;

    public BitbakeGraphTransformer(ExternalIdFactory externalIdFactory){
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(BitbakeGraph bitbakeGraph, String architecture){
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        Map<String, Dependency> namesToExternalIds = new HashMap<>();
        for (final BitbakeNode bitbakeNode : bitbakeGraph.getNodes()) {
            if (bitbakeNode.getVersion().isPresent()){
                String name = bitbakeNode.getName();
                String version = bitbakeNode.getVersion().get();
                ExternalId externalId = externalIdFactory.createArchitectureExternalId(Forge.YOCTO, name, version, architecture);
                Dependency dependency = new Dependency(name, version, externalId);
                namesToExternalIds.put(bitbakeNode.getName(), dependency);
            }
        }

        for (final BitbakeNode bitbakeNode : bitbakeGraph.getNodes()) {
            String name = bitbakeNode.getName();
            if (namesToExternalIds.containsKey(name)){
                Dependency dependency = namesToExternalIds.get(bitbakeNode.getName());
                dependencyGraph.addChildToRoot(dependency);
                for (final String child : bitbakeNode.getChildren()){
                    if (namesToExternalIds.containsKey(child)){
                        Dependency childDependency = namesToExternalIds.get(child);
                        dependencyGraph.addParentWithChild(dependency, childDependency);
                    }
                }
            }
        }

        return dependencyGraph;
    }

}
