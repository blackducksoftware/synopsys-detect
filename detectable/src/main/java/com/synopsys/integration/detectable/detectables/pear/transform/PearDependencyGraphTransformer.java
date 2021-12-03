package com.synopsys.integration.detectable.detectables.pear.transform;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;

public class PearDependencyGraphTransformer {
    private final ExternalIdFactory externalIdFactory;

    public PearDependencyGraphTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph buildDependencyGraph(Map<String, String> dependencyNameVersionMap, List<PackageDependency> packageDependencies, boolean onlyGatherRequired) {
        List<Dependency> dependencies = packageDependencies.stream()
            .filter(packageDependency -> filterRequired(packageDependency, onlyGatherRequired))
            .map(PackageDependency::getName)
            .map(dependencyName -> {
                String dependencyVersion = dependencyNameVersionMap.get(dependencyName);
                ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PEAR, dependencyName, dependencyVersion);
                return new Dependency(dependencyName, dependencyVersion, externalId);
            }).collect(Collectors.toList());

        MutableDependencyGraph mutableDependencyGraph = new MutableMapDependencyGraph();
        mutableDependencyGraph.addChildrenToRoot(dependencies);

        return mutableDependencyGraph;
    }

    private boolean filterRequired(PackageDependency packageDependency, boolean onlyGatherRequired) {
        if (onlyGatherRequired) {
            return packageDependency.isRequired();
        } else {
            return true;
        }
    }
}
