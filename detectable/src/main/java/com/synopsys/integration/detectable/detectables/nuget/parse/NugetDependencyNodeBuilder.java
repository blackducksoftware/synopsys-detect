package com.synopsys.integration.detectable.detectables.nuget.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetPackageId;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetPackageSet;

public class NugetDependencyNodeBuilder {
    private static Map<ExternalId, Dependency> externalIdToDependencyMap = new HashMap<>();
    private final List<NugetPackageSet> packageSets = new ArrayList<>();
    
    public void addPackageSets(List<NugetPackageSet> sets) {
        packageSets.addAll(sets);
    }

    public DependencyGraph createDependencyGraph(List<NugetPackageId> packageDependencies) {
        DependencyGraph graph = new BasicDependencyGraph();

        for (NugetPackageSet packageSet : packageSets) {
            if (packageSet.dependencies != null) {
                for (NugetPackageId id : packageSet.dependencies) {
                    if (packageSet.packageId != null) {
                        graph.addParentWithChild(convertPackageId(packageSet.packageId), convertPackageId(id));
                    }
                }
            }
        }

        packageDependencies.stream()
            .map(this::convertPackageId)
            .forEach(graph::addChildToRoot);

        return graph;
    }

    private Dependency convertPackageId(NugetPackageId id) {
        Dependency d  = Dependency.FACTORY.createNameVersionDependency(Forge.NUGET, id.name, id.version);
        ExternalId ei = d.getExternalId();

        if (externalIdToDependencyMap.containsKey(ei)) {
            d = externalIdToDependencyMap.get(ei);
        } else {
            externalIdToDependencyMap.put(ei, d);
        }
        return d;
    }
}
