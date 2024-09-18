package com.blackduck.integration.detect.workflow.componentlocationanalysis;

import com.blackduck.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.google.gson.JsonObject;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.blackduck.integration.detect.workflow.bdio.BdioResult;
import com.blackduck.integration.componentlocator.beans.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms {@link BdioResult} to list of {@link Component}s, which will then be used to assemble the input to
 * Component Locator.
 *
 */
public class BdioToComponentListTransformer {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Given a BDIO, creates a set containing each detected component's corresponding {@link Component} representation.
     * Each component is included once, duplicates are ignored.
     * @param bdio
     * @return set {@link Component}s
     */
    public Set<Component> transformBdioToComponentSet(BdioResult bdio) {
        List<ExternalId> allExternalIds = new ArrayList<>();

        Set<DetectCodeLocation> codeLocations = bdio.getCodeLocationNamesResult().getCodeLocationNames().keySet();
        for (DetectCodeLocation cl : codeLocations) {
            List<ExternalId> allDepsForThisCodeLocation = processDependencyGraph(cl.getDependencyGraph(), cl.getDependencyGraph().getDirectDependencies(), new HashSet<>());
            allExternalIds.addAll(allDepsForThisCodeLocation);
        }

        return externalIDsToComponentSet(allExternalIds);
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

    private Set<Component> externalIDsToComponentSet(List<ExternalId> gavs) {
        Set<Component> componentSet = new LinkedHashSet<>();
        for (ExternalId gav : gavs) {
            if (gav.getName()!=null && gav.getVersion()!=null) {
                componentSet.add(new Component(gav.getGroup(), gav.getName(), gav.getVersion(), new JsonObject()));
            } else {
                logger.warn("Invalid component entry {} from BDIO is not included for component location analysis.", gav.toString());
            }
        }
        return componentSet;
    }

    /**
     * Potential future/current(?) enhancement where if enabled through the component location analysis feature, only
     * locations of direct dependencies will be searched for. (Useful for Bridge since they only care for direct deps
     * currently + efficient when scanning large projects)
     * @param bdio
     * @return
     */
    private Set<Component> extractAndTransformDirectDependencies(BdioResult bdio) {
        Set<Component> componentSet = new LinkedHashSet<>();
        Set<DetectCodeLocation> codeLocations = bdio.getCodeLocationNamesResult().getCodeLocationNames().keySet();
        for (DetectCodeLocation cl : codeLocations) {
            cl.getDependencyGraph().getDirectDependencies()
                    .stream()
                    .map(dependency -> createLocatorComponentFrom(dependency))
                    .collect(Collectors.toCollection(() -> componentSet));
        }
        return componentSet;
    }

    private Component createLocatorComponentFrom(Dependency dep) {
        ExternalId externalId = dep.getExternalId();
        return new Component(externalId.getGroup(), externalId.getName(), externalId.getVersion(), new JsonObject());
    }
}
