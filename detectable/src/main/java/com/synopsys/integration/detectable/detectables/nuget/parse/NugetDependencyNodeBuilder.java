/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

    public NugetDependencyNodeBuilder(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public void addPackageSets(final List<NugetPackageSet> sets) {
        packageSets.addAll(sets);
    }

    public DependencyGraph createDependencyGraph(final List<NugetPackageId> packageDependencies) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (final NugetPackageSet packageSet : packageSets) {
            if (packageSet.dependencies != null) {
                for (final NugetPackageId id : packageSet.dependencies) {
                    if (packageSet.packageId != null) {
                        graph.addParentWithChild(convertPackageId(packageSet.packageId), convertPackageId(id));
                    }
                }
            }
        }

        packageDependencies.forEach(it -> graph.addChildToRoot(convertPackageId(it)));

        return graph;
    }

    private Dependency convertPackageId(final NugetPackageId id) {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NUGET, id.name, id.version);
        final Dependency node = new Dependency(id.name, id.version, externalId);
        return node;
    }
}
