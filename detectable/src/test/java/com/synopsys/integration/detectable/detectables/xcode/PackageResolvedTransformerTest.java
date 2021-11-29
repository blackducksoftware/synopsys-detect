package com.synopsys.integration.detectable.detectables.xcode;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
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
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final ExternalId swiftCollections = createId(externalIdFactory, "apple/swift-collections", "1.0.1");
    private final ExternalId auth0 = createId(externalIdFactory, "auth0/Auth0.swift", "1.35.0");
    private final ExternalId rSwiftLibrary = createId(externalIdFactory, "mac-cain13/R.swift.Library", "5.4.0");
    private final ExternalId swiftLog = createId(externalIdFactory, "apple/swift-log", "1.4.2");
    private final String defaultFileFormatVersion = PackageResolvedTransformer.KNOWN_FILE_FORMAT_VERSIONS[0];

    @Test
    void simpleTest() {
        PackageResolvedTransformer transformer = new PackageResolvedTransformer(externalIdFactory);
        PackageResolved packageResolved = createPackageResolved(null, defaultFileFormatVersion);

        DependencyGraph dependencyGraph = transformer.transform(packageResolved);
        assertDefaultPackagesExist(dependencyGraph);
    }

    @Test
    void malformedUrlTest() {
        PackageResolvedTransformer transformer = new PackageResolvedTransformer(externalIdFactory);
        PackageResolved packageResolved = createPackageResolved("some non-url gibberish", defaultFileFormatVersion);

        DependencyGraph dependencyGraph = transformer.transform(packageResolved);
        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);

        // Detect can't parse components with malformed urls. Url components are parsed for package information.
        graphAssert.hasNoDependency(swiftCollections);
        graphAssert.hasNoDependency(auth0);
        graphAssert.hasNoDependency(rSwiftLibrary);

        // Sanity check
        graphAssert.hasRootDependency(swiftLog);
        graphAssert.hasRootSize(1);
    }

    @Test
    void fileFormatVersionTest() {
        PackageResolvedTransformer transformer = new PackageResolvedTransformer(externalIdFactory);

        PackageResolved knownVersionFile = createPackageResolved(null, defaultFileFormatVersion);
        DependencyGraph knownDependencyGraph = transformer.transform(knownVersionFile);
        assertDefaultPackagesExist(knownDependencyGraph);

        // Testing that parsing continues despite unknown version. Also helps with coverage.
        PackageResolved unknownVersionFile = createPackageResolved(null, "11/2021-jm");
        DependencyGraph unknownDependencyGraph = transformer.transform(unknownVersionFile);
        assertDefaultPackagesExist(unknownDependencyGraph);
    }

    private void assertDefaultPackagesExist(DependencyGraph dependencyGraph) {
        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);
        graphAssert.hasRootDependency(swiftCollections);
        graphAssert.hasRootDependency(auth0);
        graphAssert.hasRootDependency(rSwiftLibrary);
        graphAssert.hasRootDependency(swiftLog);
        graphAssert.hasRootSize(4);
    }

    private ExternalId createId(ExternalIdFactory externalIdFactory, String name, String version) {
        return externalIdFactory.createNameVersionExternalId(Forge.GITHUB, name, version);
    }

    // The urlPrefix is so the url can be quickly malformed by the tests
    private PackageResolved createPackageResolved(@Nullable String urlPrefix, String fileFormatVersion) {
        urlPrefix = StringUtils.trimToEmpty(urlPrefix);
        ResolvedPackage swiftCollections = new ResolvedPackage(
            "swift-collections",
            urlPrefix + "https://github.com/apple/swift-collections.git",
            new PackageState(null, "2d33a0ea89c961dcb2b3da2157963d9c0370347e", "1.0.1")
        );
        ResolvedPackage auth0 = new ResolvedPackage(
            "Auth0",
            urlPrefix + "http://github.com/auth0/Auth0.swift.git",
            new PackageState(null, "8e8a6b0337a27a3342beb72b5407141fdd4a7860", "1.35.0")
        );
        ResolvedPackage rSwiftLibrary = new ResolvedPackage(
            "R.swift.Library",
            urlPrefix + "http://github.com/mac-cain13/R.swift.Library",
            new PackageState(null, "8998cfe77f4fce79ee6dfab0c88a7d551659d8fb", "5.4.0")
        );
        ResolvedPackage swiftLog = new ResolvedPackage(
            "swift-log",
            "http://github.com/apple/swift-log", // Won't be malformed for sanity check
            new PackageState(null, "5d66f7ba25daf4f94100e7022febf3c75e37a6c7", "1.4.2")
        );

        List<ResolvedPackage> resolvedPackages = Arrays.asList(swiftCollections, auth0, rSwiftLibrary, swiftLog);
        ResolvedObject resolvedObject = new ResolvedObject(resolvedPackages);
        return new PackageResolved(resolvedObject, fileFormatVersion);
    }
}