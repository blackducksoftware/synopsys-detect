package com.synopsys.integration.detectable.detectables.xcode;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.xcode.model.PackageResolved;
import com.synopsys.integration.detectable.detectables.xcode.model.PackageState;
import com.synopsys.integration.detectable.detectables.xcode.model.ResolvedObject;
import com.synopsys.integration.detectable.detectables.xcode.model.ResolvedPackage;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

class PackageResolvedTransformerTest {
    @Test
    void simpleTest() {
        ExternalIdFactory idFactory = new ExternalIdFactory();
        PackageResolvedTransformer transformer = new PackageResolvedTransformer(idFactory);
        PackageResolved packageResolved = createPackageResolved();

        DependencyGraph dependencyGraph = transformer.transform(packageResolved);
        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);

        ExternalId swiftCollections = createId(idFactory, "apple/swift-collections", "1.0.1");
        ExternalId auth0 = createId(idFactory, "auth0/Auth0.swift", "1.35.0");
        ExternalId rSwiftLibrary = createId(idFactory, "mac-cain13/R.swift.Library", "5.4.0");
        ExternalId swiftLog = createId(idFactory, "apple/swift-log", "1.4.2");

        graphAssert.hasRootDependency(swiftCollections);
        graphAssert.hasRootDependency(auth0);
        graphAssert.hasRootDependency(rSwiftLibrary);
        graphAssert.hasRootDependency(swiftLog);
        graphAssert.hasRootSize(4);
    }

    private ExternalId createId(ExternalIdFactory externalIdFactory, String name, String version) {
        return externalIdFactory.createNameVersionExternalId(Forge.GITHUB, name, version);
    }

    private PackageResolved createPackageResolved() {
        ResolvedPackage swiftCollections = new ResolvedPackage(
            "swift-collections",
            "https://github.com/apple/swift-collections.git",
            new PackageState(null, "2d33a0ea89c961dcb2b3da2157963d9c0370347e", "1.0.1")
        );
        ResolvedPackage auth0 = new ResolvedPackage(
            "Auth0",
            "http://github.com/auth0/Auth0.swift.git",
            new PackageState(null, "8e8a6b0337a27a3342beb72b5407141fdd4a7860", "1.35.0")
        );
        ResolvedPackage rSwiftLibrary = new ResolvedPackage(
            "R.swift.Library",
            "http://github.com/mac-cain13/R.swift.Library",
            new PackageState(null, "8998cfe77f4fce79ee6dfab0c88a7d551659d8fb", "5.4.0")
        );
        ResolvedPackage swiftLog = new ResolvedPackage(
            "swift-log",
            "http://github.com/apple/swift-log",
            new PackageState(null, "5d66f7ba25daf4f94100e7022febf3c75e37a6c7", "1.4.2")
        );

        List<ResolvedPackage> resolvedPackages = Arrays.asList(swiftCollections, auth0, rSwiftLibrary, swiftLog);
        ResolvedObject resolvedObject = new ResolvedObject(resolvedPackages);
        return new PackageResolved(resolvedObject, "1");
    }
}