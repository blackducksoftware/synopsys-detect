/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.nameversion;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class NameVersionNodeTransformer {
    public ExternalIdFactory externalIdFactory;

    public NameVersionNodeTransformer(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph createDependencyGraph(final Forge defaultForge, final NameVersionNode nameVersionNode) {
        return createDependencyGraph(defaultForge, nameVersionNode, true);
    }

    public DependencyGraph createDependencyGraph(final Forge defaultForge, final NameVersionNode nameVersionNode, final Boolean rootIsRealRoot) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        final Dependency root = addNameVersionNodeToDependencyGraph(graph, defaultForge, nameVersionNode);

        if (rootIsRealRoot) {
            graph.addChildToRoot(root);
        } else {
            graph.addChildrenToRoot(graph.getChildrenForParent(root));
        }
        return graph;
    }

    public Dependency addNameVersionNodeToDependencyGraph(final MutableDependencyGraph graph, final Forge defaultForge, final NameVersionNode nameVersionNode) {
        Forge forge = defaultForge;
        if (nameVersionNode.getMetadata() != null && nameVersionNode.getMetadata().getForge() != null) {
            forge = nameVersionNode.getMetadata().getForge();
        }
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(forge, nameVersionNode.getName(), nameVersionNode.getVersion());
        final Dependency parentDependency = new Dependency(nameVersionNode.getName(), nameVersionNode.getVersion(), externalId);

        for (final NameVersionNode child : nameVersionNode.getChildren()) {
            final Dependency childDependency = addNameVersionNodeToDependencyGraph(graph, defaultForge, child);
            graph.addParentWithChild(parentDependency, childDependency);
        }

        return parentDependency;
    }
}
