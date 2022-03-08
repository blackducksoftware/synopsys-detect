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

    public SwiftPackageTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation transform(SwiftPackage rootSwiftPackage) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        for (SwiftPackage swiftPackageDependency : rootSwiftPackage.getDependencies()) {
            Dependency dependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addChildToRoot(dependency);
        }

        return new CodeLocation(dependencyGraph);
    }

    private Dependency convertToDependency(MutableDependencyGraph dependencyGraph, SwiftPackage swiftPackage) {
        ExternalId externalId;
        if ("unspecified".equals(swiftPackage.getVersion())) {
            externalId = externalIdFactory.createModuleNamesExternalId(SWIFT_FORGE, swiftPackage.getName());
        } else {
            externalId = externalIdFactory.createNameVersionExternalId(SWIFT_FORGE, swiftPackage.getName(), swiftPackage.getVersion());
        }
        Dependency dependency = new Dependency(externalId);

        for (SwiftPackage swiftPackageDependency : swiftPackage.getDependencies()) {
            Dependency childDependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addParentWithChild(dependency, childDependency);
        }

        return dependency;
    }
}
