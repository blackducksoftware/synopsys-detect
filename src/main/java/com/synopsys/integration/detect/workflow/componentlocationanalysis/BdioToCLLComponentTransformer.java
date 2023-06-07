package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Transforms {@link BdioResult} to a list of {@link CLLComponent}s
 */
public class BdioToCLLComponentTransformer {
    public List<CLLComponent> transformToComponentLocatorInput(BdioResult bdio) {
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

    private CLLComponent dependencyToCLLComponent(Dependency dep) {
        return new CLLComponent(dep.getExternalId(), null);
    }

    private List<CLLComponent> externalIDsToComponentList(Set<ExternalId> gavs) {
        List<CLLComponent> componentList = new ArrayList<>();
        for (ExternalId gav : gavs) {
            componentList.add(new CLLComponent(gav, null));
        }
        return componentList;
    }
    private List<CLLComponent> onlyExtractAndTransformDirectDependencies(BdioResult bdio) {
        List<CLLComponent> componentList = new ArrayList<>();
        Set<DetectCodeLocation> codeLocations = bdio.getCodeLocationNamesResult().getCodeLocationNames().keySet();
        for (DetectCodeLocation cl : codeLocations) {
            List<CLLComponent> dependenciesPerCodeLocation = cl.getDependencyGraph().getDirectDependencies()
                    .stream()
                    .map(dependency -> dependencyToCLLComponent(dependency))
                    .collect(Collectors.toCollection(() -> componentList));
        }
        return componentList;
    }
}
