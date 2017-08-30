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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.model.NugetPackageId
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.model.NugetPackageSet

@groovy.transform.TypeChecked
public class NugetDependencyNodeBuilder {

    final List<NugetPackageSet> packageSets = new ArrayList<NugetPackageSet>()
    final Map<NugetPackageId, DependencyNode> nodeMap = new HashMap<>()

    public NugetDependencyNodeBuilder() {
    }

    public void addPackageSets(List<NugetPackageSet> sets) {
        packageSets.addAll(sets)
    }
    public void addPackageSet(NugetPackageSet set) {
        packageSets.add(set)
    }

    public Set<DependencyNode> createDependencyNodes(List<NugetPackageId> packageDependencies) {
        def nodes = new HashSet<DependencyNode>()
        packageDependencies.each {
            nodes.add(getOrCreateDependencyNode(it))
        }
        nodes
    }


    public DependencyNode getOrCreateDependencyNode(NugetPackageId id) {
        def node = nodeMap.get(id, null)
        if (node == null) {
            def externalId = new NameVersionExternalId(Forge.NUGET, id.name, id.version)
            node = new DependencyNode(id.name, id.version, externalId)
            nodeMap.put(id, node)

            //restore children
            def packageSet = packageSets.find{ set ->
                set.packageId.equals(id)
            }

            def nodeChildren = packageSet.dependencies.collect{ child ->  getOrCreateDependencyNode(child) }

            node.children.addAll(nodeChildren)

            //restore parents
            def parents = packageSets.findAll{ set ->
                set.dependencies.contains(packageSet.packageId) //all packages that depend on me. so parent.children -> me
            }

            parents.each{pkg -> getOrCreateDependencyNode(pkg.packageId).children.add(node)}
        }

        node
    }
}

