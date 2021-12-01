package com.synopsys.integration.detectable.detectables.xcode;

import java.util.Collections;
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
import com.synopsys.integration.detectable.detectables.xcode.process.PackageResolvedTransformer;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

class PackageResolvedTransformerTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    void testHttpsWithGit() {
        ResolvedPackage swiftCollectionsPackage = new ResolvedPackage(
            "swift-collections",
            "https://github.com/apple/swift-collections.git",
            new PackageState(null, "2d33a0ea89c961dcb2b3da2157963d9c0370347e", "1.0.1")
        );
        PackageResolved packageResolved = createPackageResolved(swiftCollectionsPackage);

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(externalIdFactory);
        DependencyGraph dependencyGraph = transformer.transform(packageResolved);
        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);

        ExternalId swiftCollections = externalIdFactory.createNameVersionExternalId(Forge.GITHUB, "apple/swift-collections", "1.0.1");
        graphAssert.hasRootDependency(swiftCollections);
        graphAssert.hasRootSize(1);
    }

    @Test
    void testHttpWithGit() {
        ResolvedPackage auth0Package = new ResolvedPackage(
            "Auth0",
            "http://github.com/auth0/Auth0.swift.git",
            new PackageState(null, "8e8a6b0337a27a3342beb72b5407141fdd4a7860", "1.35.0")
        );
        PackageResolved packageResolved = createPackageResolved(auth0Package);

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(externalIdFactory);
        DependencyGraph dependencyGraph = transformer.transform(packageResolved);
        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);

        ExternalId auth0 = externalIdFactory.createNameVersionExternalId(Forge.GITHUB, "auth0/Auth0.swift", "1.35.0");
        graphAssert.hasRootDependency(auth0);
        graphAssert.hasRootSize(1);
    }

    @Test
    void malformedUrlTest() {
        ResolvedPackage malformedUrlPackage = new ResolvedPackage(
            "MalformedUrlPackage",
            "data that isn't a url",
            new PackageState(null, "revision", "version")
        );

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(externalIdFactory);
        PackageResolved packageResolved = createPackageResolved(malformedUrlPackage);

        DependencyGraph dependencyGraph = transformer.transform(packageResolved);
        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);
        graphAssert.hasRootSize(0);
    }

    @Test
    void manyExtensionsTest() {
        ResolvedPackage rSwiftLibraryPackage = new ResolvedPackage(
            "R.swift.Library",
            "http://github.com/mac-cain13/R.swift.Library",
            new PackageState(null, "8998cfe77f4fce79ee6dfab0c88a7d551659d8fb", "5.4.0")
        );

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(externalIdFactory);
        PackageResolved packageResolved = createPackageResolved(rSwiftLibraryPackage);

        DependencyGraph dependencyGraph = transformer.transform(packageResolved);
        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);

        ExternalId rSwiftLibrary = externalIdFactory.createNameVersionExternalId(Forge.GITHUB, "mac-cain13/R.swift.Library", "5.4.0");
        graphAssert.hasRootDependency(rSwiftLibrary);
        graphAssert.hasRootSize(1);
    }

    @Test
    void noGitExtensionTest() {
        ResolvedPackage swiftLogPackage = new ResolvedPackage(
            "swift-log",
            "http://github.com/apple/swift-log", // Won't be malformed for sanity check
            new PackageState(null, "5d66f7ba25daf4f94100e7022febf3c75e37a6c7", "1.4.2")
        );

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(externalIdFactory);
        PackageResolved packageResolved = createPackageResolved(swiftLogPackage);

        DependencyGraph dependencyGraph = transformer.transform(packageResolved);
        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);

        ExternalId swiftLog = externalIdFactory.createNameVersionExternalId(Forge.GITHUB, "apple/swift-log", "1.4.2");
        graphAssert.hasRootDependency(swiftLog);
        graphAssert.hasRootSize(1);
    }

    private PackageResolved createPackageResolved(ResolvedPackage resolvedPackage) {
        List<ResolvedPackage> resolvedPackages = Collections.singletonList(resolvedPackage);
        ResolvedObject resolvedObject = new ResolvedObject(resolvedPackages);
        return new PackageResolved(resolvedObject, "1");
    }
}