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
package com.synopsys.integration.detectable.detectables.pip.parser;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pip.model.NameVersionCodeLocation;
import com.synopsys.integration.detectable.detectables.pip.model.PipFreeze;
import com.synopsys.integration.detectable.detectables.pip.model.PipFreezeEntry;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraph;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphDependency;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphEntry;

public class PipenvTransformer {
    private final ExternalIdFactory externalIdFactory;

    public PipenvTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public NameVersionCodeLocation transform(String projectName, String projectVersionName, PipFreeze pipFreeze, PipenvGraph pipenvGraph, boolean includeOnlyProjectTree) {
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        for (PipenvGraphEntry entry : pipenvGraph.getEntries()) {
            Dependency entryDependency = nameVersionToDependency(entry.getName(), entry.getVersion(), pipFreeze);
            List<Dependency> children = addDependenciesToGraph(entry.getChildren(), dependencyGraph, pipFreeze);
            if (matchesProject(entryDependency, projectName, projectVersionName)) { // The project appears as an entry, we don't want the project to be a dependency of itself.
                dependencyGraph.addChildrenToRoot(children);
            } else if (!includeOnlyProjectTree) { // Only add non-project matches if we are not project tree only.
                dependencyGraph.addChildToRoot(entryDependency);
                dependencyGraph.addParentWithChildren(entryDependency, children);
            }
        }

        ExternalId projectExternalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, projectName, projectVersionName);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectExternalId);
        return new NameVersionCodeLocation(projectName, projectVersionName, codeLocation);
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

    private boolean matchesProject(Dependency dependency, String projectName, String projectVersion) {
        return dependency.getName() != null && dependency.getVersion() != null && dependency.getName().equals(projectName) && dependency.getVersion().equals(projectVersion);
    }

    private String findFrozenName(String name, PipFreeze pipFreeze) {
        return pipFreeze.getEntries().stream()
                   .filter(it -> it.getName().equalsIgnoreCase(name))
                   .map(PipFreezeEntry::getName)
                   .findFirst()
                   .orElse(name);
    }

    private String findFrozenVersion(String name, String unfrozenVersion, PipFreeze pipFreeze) {
        return pipFreeze.getEntries().stream()
                   .filter(it -> it.getName().equalsIgnoreCase(name))
                   .map(PipFreezeEntry::getVersion)
                   .findFirst()
                   .orElse(unfrozenVersion);
    }

    private Dependency nameVersionToDependency(String givenName, String givenVersion, PipFreeze pipFreeze) {
        String version = findFrozenVersion(givenName, givenVersion, pipFreeze);
        String name = findFrozenName(givenName, pipFreeze);
        return new Dependency(name, version, externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version));
    }
}
