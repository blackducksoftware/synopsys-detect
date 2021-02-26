/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

    public PearDependencyGraphTransformer(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph buildDependencyGraph(final Map<String, String> dependencyNameVersionMap, final List<PackageDependency> packageDependencies, final boolean onlyGatherRequired) {
        final List<Dependency> dependencies = packageDependencies.stream()
                                                  .filter(packageDependency -> filterRequired(packageDependency, onlyGatherRequired))
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

    private boolean filterRequired(final PackageDependency packageDependency, final boolean onlyGatherRequired) {
        if (onlyGatherRequired) {
            return packageDependency.isRequired();
        } else {
            return true;
        }
    }
}
