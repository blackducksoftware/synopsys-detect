package com.synopsys.integration.detectable.detectables.nuget.parse;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetPackageId;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetPackageSet;

public class NugetDependencyNodeBuilder {
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
        return Dependency.FACTORY.createNameVersionDependency(Forge.NUGET, id.name, id.version);
    }
}
