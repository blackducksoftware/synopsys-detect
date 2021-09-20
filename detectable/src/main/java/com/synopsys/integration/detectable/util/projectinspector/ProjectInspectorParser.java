/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.util.projectinspector;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.util.projectinspector.model.ProjectInspectorModule;
import com.synopsys.integration.detectable.util.projectinspector.model.ProjectInspectorOutput;

public class ProjectInspectorParser {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public ProjectInspectorParser(Gson gson, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public List<CodeLocation> parse(String inspectionOutput) {
        ProjectInspectorOutput projectInspectorOutput = gson.fromJson(inspectionOutput, ProjectInspectorOutput.class);

        return projectInspectorOutput.modules.values().stream()
                   .map(this::codeLocationFromModule)
                   .collect(Collectors.toList());
    }

    public CodeLocation codeLocationFromModule(ProjectInspectorModule module) {
        Map<String, Dependency> lookup = new HashMap<>();

        //build the map of all external ids
        module.dependencies.forEach(dependency -> lookup.computeIfAbsent(dependency.id, it -> {
            return new Dependency(dependency.name, dependency.version, externalIdFactory.createNameVersionExternalId(Forge.NUGET, dependency.name, dependency.version));
        }));

        //and add them to the graph
        MutableDependencyGraph mutableDependencyGraph = new MutableMapDependencyGraph();
        module.dependencies.forEach(dependency -> {
            Dependency graphDependency = lookup.get(dependency.id);
            dependency.includedBy.forEach(parent -> {
                if (parent.equals("DIRECT")) {
                    mutableDependencyGraph.addChildToRoot(graphDependency);
                } else {
                    mutableDependencyGraph.addChildWithParent(graphDependency, lookup.get(parent));
                }
            });
        });
        return new CodeLocation(mutableDependencyGraph, new File(module.moduleFile));
    }
}
