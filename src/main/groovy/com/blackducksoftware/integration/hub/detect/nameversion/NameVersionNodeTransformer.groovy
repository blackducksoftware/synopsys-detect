/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.nameversion

import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NameVersionNodeTransformer {
    public ExternalIdFactory externalIdFactory;
    public NameVersionNodeTransformer(ExternalIdFactory externalIdFactory){
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph createDependencyGraph(Forge defaultForge, NameVersionNode nameVersionNode) {
        return createDependencyGraph(defaultForge, nameVersionNode, true)
    }

    public DependencyGraph createDependencyGraph(Forge defaultForge, NameVersionNode nameVersionNode, Boolean rootIsRealRoot) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph()

        def root = addNameVersionNodeToDependencyGraph(graph, defaultForge, nameVersionNode)

        if (rootIsRealRoot){
            graph.addChildToRoot(root);
        }else{
            graph.addChildrenToRoot(graph.getChildrenForParent(root))
        }
        return graph
    }

    public Dependency addNameVersionNodeToDependencyGraph(MutableDependencyGraph graph, Forge defaultForge, NameVersionNode nameVersionNode) {
        final Forge forge = nameVersionNode.metadata?.forge ? nameVersionNode.metadata.forge : defaultForge
        def externalId = externalIdFactory.createNameVersionExternalId(forge, nameVersionNode.name, nameVersionNode.version)
        def parentDependency = new Dependency(nameVersionNode.name, nameVersionNode.version, externalId)

        nameVersionNode.children.each {
            def childDependency = addNameVersionNodeToDependencyGraph(graph, defaultForge, it);
            graph.addParentWithChild(parentDependency, childDependency);
        }

        parentDependency
    }
}
