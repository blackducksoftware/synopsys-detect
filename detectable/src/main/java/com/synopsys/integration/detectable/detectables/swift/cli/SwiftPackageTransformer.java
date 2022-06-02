package com.synopsys.integration.detectable.detectables.swift.cli;

import java.net.MalformedURLException;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.swift.cli.model.SwiftPackage;

public class SwiftPackageTransformer {

    private final GitUrlParser gitUrlParser;

    public SwiftPackageTransformer(GitUrlParser gitUrlParser) {
        this.gitUrlParser = gitUrlParser;
    }

    public CodeLocation transform(SwiftPackage rootSwiftPackage) throws MalformedURLException {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        for (SwiftPackage swiftPackageDependency : rootSwiftPackage.getDependencies()) {
            Dependency dependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addDirectDependency(dependency);
        }

        return new CodeLocation(dependencyGraph);
    }

    private Dependency convertToDependency(DependencyGraph dependencyGraph, SwiftPackage swiftPackage) throws MalformedURLException {
        ExternalId externalId = createExternalId(swiftPackage);
        Dependency dependency = new Dependency(externalId);

        for (SwiftPackage swiftPackageDependency : swiftPackage.getDependencies()) {
            Dependency childDependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addParentWithChild(dependency, childDependency);
        }

        return dependency;
    }

    private ExternalId createExternalId(SwiftPackage swiftPackage) throws MalformedURLException {
        ExternalId externalId;
        Forge forge = Forge.COCOAPODS;
        String packageName = swiftPackage.getName();
        if (swiftPackage.getUrl().isPresent()) {
            packageName = gitUrlParser.getRepoName(swiftPackage.getUrl().get());
            forge = Forge.GITHUB;
        }

        if ("unspecified".equals(swiftPackage.getVersion())) {
            externalId = ExternalId.FACTORY.createModuleNamesExternalId(forge, packageName);
        } else {
            externalId = ExternalId.FACTORY.createNameVersionExternalId(forge, packageName, swiftPackage.getVersion());
        }

        return externalId;
    }
}
