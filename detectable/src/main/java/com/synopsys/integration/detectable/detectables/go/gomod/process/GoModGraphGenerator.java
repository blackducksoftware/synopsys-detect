/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod.process;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoGraphRelationship;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListModule;

public class GoModGraphGenerator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;
    private final Set<String> fullyGraphedModules = new HashSet<>();

    public GoModGraphGenerator(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation generateGraph(GoListModule projectModule, GoRelationshipManager goRelationshipManager, GoModDependencyManager goModDependencyManager) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        String moduleName = projectModule.getPath();
        if (goRelationshipManager.hasRelationshipsFor(moduleName)) {
            goRelationshipManager.getRelationshipsFor(moduleName).stream()
                .map(relationship -> relationship.getChild().getName())
                .forEach(childName -> addModuleToGraph(childName, null, graph, goRelationshipManager, goModDependencyManager));
        }

        return new CodeLocation(graph, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, projectModule.getPath(), projectModule.getVersion()));
    }

    private void addModuleToGraph(String moduleName, @Nullable Dependency parent, MutableDependencyGraph graph, GoRelationshipManager goRelationshipManager, GoModDependencyManager goModDependencyManager) {
        if (parent == null && goRelationshipManager.isNotUsedByMainModule(moduleName)) {
            logger.debug("Excluding module '{}' because it is not used by the main module.", moduleName);
            return;
        }

        Dependency dependency = goModDependencyManager.getDependencyForModule(moduleName);
        if (parent != null) {
            graph.addChildWithParent(dependency, parent);
        } else {
            graph.addChildToRoot(dependency);
        }

        if (!fullyGraphedModules.contains(moduleName) && goRelationshipManager.hasRelationshipsFor(moduleName)) {
            fullyGraphedModules.add(moduleName);
            List<GoGraphRelationship> projectRelationships = goRelationshipManager.getRelationshipsFor(moduleName);
            for (GoGraphRelationship projectRelationship : projectRelationships) {
                addModuleToGraph(projectRelationship.getChild().getName(), dependency, graph, goRelationshipManager, goModDependencyManager);
            }
        }
    }
}
