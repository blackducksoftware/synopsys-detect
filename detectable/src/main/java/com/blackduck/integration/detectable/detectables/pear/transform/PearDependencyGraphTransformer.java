package com.blackduck.integration.detectable.detectables.pear.transform;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackduck.integration.bdio.graph.BasicDependencyGraph;
import com.blackduck.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.bdio.model.Forge;
import com.blackduck.integration.bdio.model.dependency.Dependency;
import com.blackduck.integration.bdio.model.externalid.ExternalId;
import com.blackduck.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.pear.PearDependencyType;
import com.blackduck.integration.detectable.detectables.pear.model.PackageDependency;

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
