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

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.model.NugetPackageId
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.model.NugetPackageSet

import groovy.transform.TypeChecked

@TypeChecked
public class NugetDependencyNodeBuilder {

    final List<NugetPackageSet> packageSets = new ArrayList<NugetPackageSet>()


    public ExternalIdFactory externalIdFactory;
    public NugetDependencyNodeBuilder(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public void addPackageSets(List<NugetPackageSet> sets) {
        packageSets.addAll(sets)
    }
    public void addPackageSet(NugetPackageSet set) {
        packageSets.add(set)
    }

    public DependencyGraph createDependencyGraph(List<NugetPackageId> packageDependencies) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (def packageSet : packageSets) {
            for (def id : packageSet.dependencies) {
                graph.addParentWithChild(convertPackageId(packageSet.getPackageId()), convertPackageId(id))
            }
        }

        packageDependencies.each {
            graph.addChildToRoot(convertPackageId(it))
        }

        graph
    }

    private Dependency convertPackageId(NugetPackageId id) {
        def externalId = externalIdFactory.createNameVersionExternalId(Forge.NUGET, id.name, id.version)
        def node = new Dependency(id.name, id.version, externalId)
        node
    }
}

