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
import com.synopsys.integration.detectable.detectables.pear.PearCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;

public class PearDependencyGraphTransformer {
    private final ExternalIdFactory externalIdFactory;
    private final PearCliDetectableOptions pearCliDetectableOptions;

    public PearDependencyGraphTransformer(final ExternalIdFactory externalIdFactory, final PearCliDetectableOptions pearCliDetectableOptions) {
        this.externalIdFactory = externalIdFactory;
        this.pearCliDetectableOptions = pearCliDetectableOptions;
    }

    public DependencyGraph buildDependencyGraph(final Map<String, String> dependencyNameVersionMap, final List<PackageDependency> packageDependencies) {
        final List<Dependency> dependencies = packageDependencies.stream()
                                                  .filter(this::filterRequired)
                                                  .map(PackageDependency::getName)
                                                  .map(dependencyName -> {
                                                      final String dependencyVersion = dependencyNameVersionMap.get(dependencyName);
                                                      final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PEAR, dependencyName, dependencyVersion);
                                                      return new Dependency(dependencyName, dependencyVersion, externalId);
                                                  }).collect(Collectors.toList());

        final MutableDependencyGraph mutableDependencyGraph = new MutableMapDependencyGraph();
        mutableDependencyGraph.addChildrenToRoot(dependencies);

        return mutableDependencyGraph;
    }

    private boolean filterRequired(final PackageDependency packageDependency) {
        if (pearCliDetectableOptions.onlyGatherRequired()) {
            return packageDependency.isRequired();
        } else {
            return true;
        }
    }
}
