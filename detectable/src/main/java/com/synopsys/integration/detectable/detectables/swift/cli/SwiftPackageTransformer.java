package com.synopsys.integration.detectable.detectables.swift.cli;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.swift.cli.model.SwiftPackage;

public class SwiftPackageTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GitUrlParser gitUrlParser;

    public SwiftPackageTransformer(GitUrlParser gitUrlParser) {
        this.gitUrlParser = gitUrlParser;
    }

    public CodeLocation transform(SwiftPackage rootSwiftPackage) {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        for (SwiftPackage swiftPackageDependency : rootSwiftPackage.getDependencies()) {
            Dependency dependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addDirectDependency(dependency);
        }

        return new CodeLocation(dependencyGraph);
    }

    private Dependency convertToDependency(DependencyGraph dependencyGraph, SwiftPackage swiftPackage) {
        ExternalId externalId = createExternalId(swiftPackage);
        Dependency dependency = new Dependency(externalId);

        for (SwiftPackage swiftPackageDependency : swiftPackage.getDependencies()) {
            Dependency childDependency = convertToDependency(dependencyGraph, swiftPackageDependency);
            dependencyGraph.addParentWithChild(dependency, childDependency);
        }

        return dependency;
    }

    private ExternalId createExternalId(SwiftPackage swiftPackage) {
        ExternalId externalId;
        Forge forge;
        String packageName;

        try {
            forge = Forge.GITHUB;
            packageName = gitUrlParser.getRepoName(swiftPackage.getUrl());
        } catch (MalformedURLException e) {
            forge = Forge.COCOAPODS;
            packageName = swiftPackage.getName();
            logger.warn("Failed to parse url for package {}. Falling back to ExternalId which may not match a component. Please contact support", packageName, e);
        }

        if ("unspecified".equals(swiftPackage.getVersion())) {
            externalId = ExternalId.FACTORY.createModuleNamesExternalId(forge, packageName);
        } else {
            externalId = ExternalId.FACTORY.createNameVersionExternalId(forge, packageName, swiftPackage.getVersion());
        }

        return externalId;
    }
}
