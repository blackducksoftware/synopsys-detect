package com.synopsys.integration.detectable.detectables.projectinspector;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.projectinspector.model.ProjectInspectorDependency;
import com.synopsys.integration.detectable.detectables.projectinspector.model.ProjectInspectorMavenCoordinate;
import com.synopsys.integration.detectable.detectables.projectinspector.model.ProjectInspectorModule;
import com.synopsys.integration.detectable.detectables.projectinspector.model.ProjectInspectorOutput;

// TODO: Should be split into a Parser/Transformer
public class ProjectInspectorParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public ProjectInspectorParser(Gson gson, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public List<CodeLocation> parse(String inspectionOutput) {
        ProjectInspectorOutput projectInspectorOutput = gson.fromJson(inspectionOutput, ProjectInspectorOutput.class);
        // TODO: Could maybe use some null-safety around the presence of the 'modules' field
        //  There should be more safety (at least @Nullable) in all the ProjectInspector model objects
        return projectInspectorOutput.modules.values().stream()
            .map(this::codeLocationFromModule)
            .collect(Collectors.toList());
    }

    public CodeLocation codeLocationFromModule(ProjectInspectorModule module) {
        Map<String, Dependency> lookup = new HashMap<>();

        //build the map of all external ids
        module.dependencies.forEach(dependency -> lookup.computeIfAbsent(dependency.id, missingId -> convertProjectInspectorDependency(dependency)));

        //and add them to the graph
        DependencyGraph mutableDependencyGraph = new BasicDependencyGraph();
        module.dependencies.forEach(moduleDependency -> {
            Dependency dependency = lookup.get(moduleDependency.id);
            moduleDependency.includedBy.forEach(parent -> {
                if ("DIRECT".equals(parent)) {
                    mutableDependencyGraph.addDirectDependency(dependency);
                } else if (lookup.containsKey(parent)) {
                    mutableDependencyGraph.addChildWithParent(dependency, lookup.get(parent));
                } else { //Theoretically should not happen according to PI devs. -jp
                    throw new RuntimeException("An error occurred reading the project inspector output." +
                        " An unknown parent dependency was encountered '" + parent + "' while including dependency '" + moduleDependency.name + "'.");
                }
            });
        });
        return new CodeLocation(mutableDependencyGraph, new File(module.moduleFile));
    }

    public Dependency convertProjectInspectorDependency(ProjectInspectorDependency dependency) {
        if ("MAVEN".equals(dependency.dependencyType) && dependency.mavenCoordinate != null) {
            ProjectInspectorMavenCoordinate gav = dependency.mavenCoordinate;
            return new Dependency(gav.artifact, gav.version, externalIdFactory.createMavenExternalId(gav.group, gav.artifact, gav.version));
        } else if ("MAVEN".equals(dependency.dependencyType)) {
            logger.warn("Project Inspector Maven dependency did not have coordinates, using name and version only.");
            return new Dependency(dependency.name, dependency.version, externalIdFactory.createNameVersionExternalId(Forge.MAVEN, dependency.name, dependency.version));
        } else if ("NUGET".equals(dependency.dependencyType)) {
            return new Dependency(dependency.name, dependency.version, externalIdFactory.createNameVersionExternalId(Forge.NUGET, dependency.name, dependency.version));
        } else {
            throw new RuntimeException("Unknown Project Inspector dependency type: " + dependency.dependencyType);
        }
    }
}
