package com.synopsys.integration.detectable.detectables.pear.transform;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pear.PearDependencyType;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;

public class PearDependencyGraphTransformer {
    private final ExternalIdFactory externalIdFactory;
    private final EnumListFilter<PearDependencyType> pearDependencyTypeFilter;

    public PearDependencyGraphTransformer(ExternalIdFactory externalIdFactory, EnumListFilter<PearDependencyType> pearDependencyTypeFilter) {
        this.externalIdFactory = externalIdFactory;
        this.pearDependencyTypeFilter = pearDependencyTypeFilter;
    }

    public DependencyGraph buildDependencyGraph(Map<String, String> dependencyNameVersionMap, List<PackageDependency> packageDependencies) {
        List<Dependency> dependencies = packageDependencies.stream()
            .filter(this::filterDependencyType)
            .map(PackageDependency::getName)
            .map(dependencyName -> {
                String dependencyVersion = dependencyNameVersionMap.get(dependencyName);
                ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PEAR, dependencyName, dependencyVersion);
                return new Dependency(dependencyName, dependencyVersion, externalId);
            }).collect(Collectors.toList());

        DependencyGraph mutableDependencyGraph = new BasicDependencyGraph();
        mutableDependencyGraph.addChildrenToRoot(dependencies);

        return mutableDependencyGraph;
    }

    private boolean filterDependencyType(PackageDependency packageDependency) {
        return packageDependency.isRequired() || pearDependencyTypeFilter.shouldInclude(PearDependencyType.OPTIONAL);
    }
}
