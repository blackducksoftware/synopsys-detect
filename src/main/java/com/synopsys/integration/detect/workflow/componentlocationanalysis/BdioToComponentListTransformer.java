package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transforms {@link BdioResult} to list of {@link Component}s, which will then be used to assemble the input to
 * Component Locator.
 *
 */
public class BdioToComponentListTransformer {
    public List<Component> transformBdioToComponentList(BdioResult bdio) {
        Set<ExternalId> externalIds = new HashSet<>();

        Set<DetectCodeLocation> codeLocations = bdio.getCodeLocationNamesResult().getCodeLocationNames().keySet();
        for (DetectCodeLocation cl : codeLocations) {
            Set<Dependency> directDepsPerCodeLocation = cl.getDependencyGraph().getDirectDependencies();

            // add all direct deps to component list
            cl.getDependencyGraph().getDirectDependencies()
                    .stream()
                    .map(dependency -> dependency.getExternalId())
                    .collect(Collectors.toCollection(() -> externalIds));

            // now for each dir dep, add its children to component list too
            for (Dependency dirDep : directDepsPerCodeLocation) {
                cl.getDependencyGraph().getChildrenForParent(dirDep)
                        .stream()
                        .map(dependency -> dependency.getExternalId())
                        .collect(Collectors.toCollection(() -> externalIds));
            }
        }
        return externalIDsToComponentList(externalIds);
    }

    private Component dependencyToCLLComponent(Dependency dep) {
        return new Component(dep.getExternalId(), null);
    }

    // TODO move me to a util class
    private List<Component> externalIDsToComponentList(Set<ExternalId> gavs) {
        List<Component> componentList = new ArrayList<>();
        for (ExternalId gav : gavs) {
            componentList.add(new Component(gav, null));
        }
        return componentList;
    }

    /**
     * Maybe a future enhancement where if enabled, we only look for direct dependencies (could be useful for Bridge
     * since they only care for direct deps currently)
     * @param bdio
     * @return
     */
    private List<Component> onlyExtractAndTransformDirectDependencies(BdioResult bdio) {
        List<Component> componentList = new ArrayList<>();
        Set<DetectCodeLocation> codeLocations = bdio.getCodeLocationNamesResult().getCodeLocationNames().keySet();
        for (DetectCodeLocation cl : codeLocations) {
            List<Component> dependenciesPerCodeLocation = cl.getDependencyGraph().getDirectDependencies()
                    .stream()
                    .map(dependency -> dependencyToCLLComponent(dependency))
                    .collect(Collectors.toCollection(() -> componentList));
        }
        return componentList;
    }
}
