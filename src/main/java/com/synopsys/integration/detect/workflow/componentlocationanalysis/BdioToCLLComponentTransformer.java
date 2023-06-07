package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Transforms {@link BdioResult} to a list of {@link CLLComponent}s
 */
public class BdioToCLLComponentTransformer {
    private List<CLLComponent> componentList;
    public List<CLLComponent> transformToComponentLocatorInput(BdioResult bdio) {
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

    private CLLComponent dependencyToCLLComponent(Dependency dep) {
        return new CLLComponent(dep.getExternalId(), null);
    }
}
