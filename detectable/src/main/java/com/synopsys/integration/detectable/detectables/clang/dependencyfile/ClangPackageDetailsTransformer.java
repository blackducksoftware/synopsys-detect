/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.dependencyfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

public class ClangPackageDetailsTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public ClangPackageDetailsTransformer(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation toCodeLocation(final List<Forge> dependencyForges, final Set<PackageDetails> packages) {
        final List<Dependency> dependencies = packages.parallelStream()
                                                  .flatMap(pkg -> toDependency(dependencyForges, pkg).stream())
                                                  .collect(Collectors.toList());
        logger.trace("Generated : " + dependencies.size() + " dependencies.");

        final MutableDependencyGraph dependencyGraph = populateGraph(dependencies);
        return new CodeLocation(dependencyGraph);
    }

    private List<Dependency> toDependency(final List<Forge> forges, final PackageDetails details) {
        final String name = details.getPackageName();
        final String version = details.getPackageVersion();
        final String arch = details.getPackageArch();

        final List<Dependency> dependencies = new ArrayList<>();
        final String externalId = String.format("%s/%s/%s", name, version, arch);
        logger.trace(String.format("Constructed externalId: %s", externalId));
        for (final Forge forge : forges) {
            final ExternalId extId = externalIdFactory.createArchitectureExternalId(forge, name, version, arch);
            final Dependency dep = new Dependency(name, version, extId);
            logger.debug(String.format("forge: %s: adding %s version %s as child to dependency node tree; externalId: %s", forge.getName(), dep.getName(), dep.getVersion(), dep.getExternalId().createBdioId()));
            dependencies.add(dep);
        }
        return dependencies;
    }

    private MutableDependencyGraph populateGraph(final List<Dependency> bdioComponents) {
        final MutableDependencyGraph dependencyGraph = new SimpleBdioFactory().createMutableDependencyGraph();
        for (final Dependency bdioComponent : bdioComponents) {
            dependencyGraph.addChildToRoot(bdioComponent);
        }
        return dependencyGraph;
    }
}
