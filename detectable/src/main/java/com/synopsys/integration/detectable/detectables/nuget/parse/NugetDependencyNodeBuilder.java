package com.synopsys.integration.detectable.detectables.nuget.parse;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetPackageId;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetPackageSet;

public class NugetDependencyNodeBuilder {
    private final List<NugetPackageSet> packageSets = new ArrayList<>();

    private final ExternalIdFactory externalIdFactory;

    public NugetDependencyNodeBuilder(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public void addPackageSets(List<NugetPackageSet> sets) {
        packageSets.addAll(sets);
    }

    public DependencyGraph createDependencyGraph(List<NugetPackageId> packageDependencies) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (NugetPackageSet packageSet : packageSets) {
            if (packageSet.dependencies != null) {
                for (NugetPackageId id : packageSet.dependencies) {
                    if (packageSet.packageId != null) {
                        graph.addParentWithChild(convertPackageId(packageSet.packageId), convertPackageId(id));
                    }
                }
            }
        }

        packageDependencies.forEach(it -> graph.addChildToRoot(convertPackageId(it)));

        return graph;
    }

    private Dependency convertPackageId(NugetPackageId id) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NUGET, id.name, id.version);
        Dependency node = new Dependency(id.name, id.version, externalId);
        return node;
    }
}
