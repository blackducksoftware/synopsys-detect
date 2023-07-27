package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.google.gson.JsonObject;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.componentlocator.beans.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Transforms {@link BdioResult} to list of {@link Component}s, which will then be used to assemble the input to
 * Component Locator.
 *
 */
public class BdioToComponentListTransformer {
    /**
     * Given a BDIO, creates a list containing each detected component's corresponding {@link Component} representation.
     * Each component is included once, duplicates are ignored.
     * @param bdio
     * @return list of unique {@link Component}s
     */
    public List<Component> transformBdioToComponentList(BdioResult bdio) {
        List<ExternalId> allExternalIds = new ArrayList<>();

        Set<DetectCodeLocation> codeLocations = bdio.getCodeLocationNamesResult().getCodeLocationNames().keySet();
        for (DetectCodeLocation cl : codeLocations) {
            List<ExternalId> allDepsForThisCodeLocation = processDependencyGraph(cl.getDependencyGraph(), cl.getDependencyGraph().getDirectDependencies(), new HashSet<>());
            allExternalIds.addAll(allDepsForThisCodeLocation);
        }

        return externalIDsToComponentList(allExternalIds);
    }

    /**
     * Starting with a list of the direct dependencies of a code location, recursively collects {@link ExternalId}s of
     * all direct and transitive dependencies.
     * @param graph
     * @param dependencies
     * @param alreadyProcessedDependencies
     * @return list of all dependencies in the original dependency graph
     */
    private List<ExternalId> processDependencyGraph(
            DependencyGraph graph,
            Set<Dependency> dependencies,
            HashSet<ExternalId> alreadyProcessedDependencies
    ) {
        List<ExternalId> addedDependencies = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            if (!alreadyProcessedDependencies.contains(dependency.getExternalId())) {
                addedDependencies.add(dependency.getExternalId());
                alreadyProcessedDependencies.add(dependency.getExternalId());
                Set<Dependency> transitiveDependencies = graph.getChildrenForParent(dependency);
                List<ExternalId> addedChildren = processDependencyGraph(graph, transitiveDependencies, alreadyProcessedDependencies);
                addedDependencies.addAll(addedChildren);
            }
        }
        return addedDependencies;
    }

    private List<Component> externalIDsToComponentList(List<ExternalId> gavs) {
        List<Component> componentList = new ArrayList<>();
        for (ExternalId gav : gavs) {
            componentList.add(new Component(gav.getGroup(), gav.getName(), gav.getVersion(), new JsonObject()));
        }
        return componentList;
    }

    /**
     * Potential future/current(?) enhancement where if enabled through the component location analysis feature, only
     * locations of direct dependencies will be searched for. (Useful for Bridge since they only care for direct deps
     * currently + efficient when scanning large projects)
     * @param bdio
     * @return
     */
    private List<Component> extractAndTransformDirectDependencies(BdioResult bdio) {
        List<Component> componentList = new ArrayList<>();
        Set<DetectCodeLocation> codeLocations = bdio.getCodeLocationNamesResult().getCodeLocationNames().keySet();
        for (DetectCodeLocation cl : codeLocations) {
            List<Component> dependenciesPerCodeLocation = cl.getDependencyGraph().getDirectDependencies()
                    .stream()
                    .map(dependency -> createLocatorComponentFrom(dependency))
                    .collect(Collectors.toCollection(() -> componentList));
        }
        return componentList;
    }

    private Component createLocatorComponentFrom(Dependency dep) {
        ExternalId externalId = dep.getExternalId();
        return new Component(externalId.getGroup(), externalId.getName(), externalId.getVersion(), new JsonObject());
    }
}
