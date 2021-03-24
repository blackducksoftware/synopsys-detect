/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.swift;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.swift.model.SwiftPackage;

public class SwiftPackageTransformer {
    public static final Forge SWIFT_FORGE = Forge.COCOAPODS;

    private final ExternalIdFactory externalIdFactory;

    public SwiftPackageTransformer(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation transform(final SwiftPackage rootSwiftPackage) {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        for (final SwiftPackage swiftPackageDependency : rootSwiftPackage.getDependencies()) {
            final Dependency dependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addChildToRoot(dependency);
        }

        return new CodeLocation(dependencyGraph);
    }

    private Dependency convertToDependency(final MutableDependencyGraph dependencyGraph, final SwiftPackage swiftPackage) {
        final ExternalId externalId;
        if ("unspecified".equals(swiftPackage.getVersion())) {
            externalId = externalIdFactory.createModuleNamesExternalId(SWIFT_FORGE, swiftPackage.getName());
        } else {
            externalId = externalIdFactory.createNameVersionExternalId(SWIFT_FORGE, swiftPackage.getName(), swiftPackage.getVersion());
        }
        final Dependency dependency = new Dependency(externalId);

        for (final SwiftPackage swiftPackageDependency : swiftPackage.getDependencies()) {
            final Dependency childDependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addParentWithChild(dependency, childDependency);
        }

        return dependency;
    }
}
