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
package com.synopsys.integration.detectable.detectables.pip.parser;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pip.model.PipFreeze;
import com.synopsys.integration.detectable.detectables.pip.model.PipFreezeEntry;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvResult;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraph;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphDependency;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphEntry;

public class PipenvTransformer {
    private final ExternalIdFactory externalIdFactory;

    public PipenvTransformer(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public PipenvResult transform(final String projectName, final String projectVersionName, final PipFreeze pipFreeze, final PipenvGraph pipenvGraph) {
        final MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        for (PipenvGraphEntry entry : pipenvGraph.getEntries()){
            Dependency entryDependency = nameVersionToDependency(entry.getName(), entry.getVersion(), pipFreeze);
            List<Dependency> children = addDependenciesToGraph(entry.getChildren(), dependencyGraph, pipFreeze);
            if (matchesProject(entryDependency, projectName, projectVersionName)) { //the project appears as an entry, we don't want the project to be a dependency of itself
                dependencyGraph.addChildrenToRoot(children);
            } else {
                dependencyGraph.addChildToRoot(entryDependency);
                dependencyGraph.addParentWithChildren(entryDependency, children);
            }
        }

        if (!dependencyGraph.getRootDependencyExternalIds().isEmpty()) {
            final ExternalId projectExternalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, projectName, projectVersionName);
            final CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectExternalId);
            return new PipenvResult(projectName, projectVersionName, codeLocation);
        } else {
            return null;
        }
    }

    private List<Dependency> addDependenciesToGraph(List<PipenvGraphDependency> graphDependencies, MutableMapDependencyGraph graph, PipFreeze pipFreeze) {
        List<Dependency> dependencies = new ArrayList<>();
        for (PipenvGraphDependency graphDependency : graphDependencies) {
            Dependency dependency = nameVersionToDependency(graphDependency.getName(), graphDependency.getInstalledVersion(), pipFreeze);
            List<Dependency> children = addDependenciesToGraph(graphDependency.getChildren(), graph, pipFreeze);
            graph.addParentWithChildren(dependency, children);
            dependencies.add(dependency);
        }
        return dependencies;
    }

    private boolean matchesProject(final Dependency dependency, final String projectName, final String projectVersion) {
        return dependency.name != null && dependency.version != null && dependency.name.equals(projectName) && dependency.version.equals(projectVersion);
    }

    private String findFrozenName(String name, PipFreeze pipFreeze) {
        return pipFreeze.getEntries().stream()
                   .filter(it -> it.getName().toLowerCase().equals(name.toLowerCase()))
                   .map(PipFreezeEntry::getName)
                   .findFirst()
                   .orElse(name);
    }

    private String findFrozenVersion(String name, String unfrozenVersion, PipFreeze pipFreeze) {
        return pipFreeze.getEntries().stream()
                   .filter(it -> it.getName().toLowerCase().equals(name.toLowerCase()))
                   .map(PipFreezeEntry::getVersion)
                   .findFirst()
                   .orElse(unfrozenVersion);
    }

    private Dependency nameVersionToDependency(String givenName, String givenVersion, PipFreeze pipFreeze){
        String version = findFrozenVersion(givenName, givenVersion, pipFreeze);
        String name = findFrozenName(givenName, pipFreeze);
        return new Dependency(name, version, externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version));
    }
}
