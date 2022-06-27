package com.synopsys.integration.detectable.detectables.swift.lock.transform;

import static com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat.V_1;
import static com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat.V_2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectables.git.cli.GitUrlParser;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageResolvedFormat;
import com.synopsys.integration.detectable.detectables.swift.lock.data.PackageState;
import com.synopsys.integration.detectable.detectables.swift.lock.data.ResolvedPackage;
import com.synopsys.integration.detectable.detectables.swift.lock.parse.PackageResolvedFormatChecker;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

class PackageResolvedTransformerTest {
    private final static String PACKAGE_KIND = "remoteSourceControl";

    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void testHttpsWithGit(PackageResolvedFormat testedFormat) {
        ResolvedPackage swiftCollectionsPackage = ResolvedPackage.version2(
            "swift-collections",
            "https://github.com/apple/swift-collections.git",
            PACKAGE_KIND,
            new PackageState(null, "2d33a0ea89c961dcb2b3da2157963d9c0370347e", "1.0.1")
        );
        List<ResolvedPackage> resolvedPackages = createResolvedPackagesForFormat(testedFormat, swiftCollectionsPackage);

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(new GitUrlParser());
        DependencyGraph dependencyGraph = transformer.transform(resolvedPackages);

        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);
        ExternalId swiftCollections = ExternalId.FACTORY.createNameVersionExternalId(Forge.GITHUB, "apple/swift-collections", "1.0.1");
        graphAssert.hasRootDependency(swiftCollections);
        graphAssert.hasRootSize(1);
    }

    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void testHttpWithGit(PackageResolvedFormat testedFormat) {
        ResolvedPackage auth0Package = ResolvedPackage.version2(
            "Auth0",
            "http://github.com/auth0/Auth0.swift.git",
            PACKAGE_KIND,
            new PackageState(null, "8e8a6b0337a27a3342beb72b5407141fdd4a7860", "1.35.0")
        );
        List<ResolvedPackage> resolvedPackages = createResolvedPackagesForFormat(testedFormat, auth0Package);

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(new GitUrlParser());
        DependencyGraph dependencyGraph = transformer.transform(resolvedPackages);

        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);
        ExternalId auth0 = ExternalId.FACTORY.createNameVersionExternalId(Forge.GITHUB, "auth0/Auth0.swift", "1.35.0");
        graphAssert.hasRootDependency(auth0);
        graphAssert.hasRootSize(1);
    }

    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void malformedUrlTest(PackageResolvedFormat testedFormat) {
        ResolvedPackage malformedUrlPackage = ResolvedPackage.version2(
            "MalformedUrlPackage",
            "data that isn't a url",
            PACKAGE_KIND,
            new PackageState(null, "revision", "version")
        );
        List<ResolvedPackage> resolvedPackages = createResolvedPackagesForFormat(testedFormat, malformedUrlPackage);

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(new GitUrlParser());
        DependencyGraph dependencyGraph = transformer.transform(resolvedPackages);

        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);
        graphAssert.hasRootSize(0);
    }

    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void manyExtensionsTest(PackageResolvedFormat testedFormat) {
        ResolvedPackage rSwiftLibraryPackage = ResolvedPackage.version2(
            "R.swift.Library",
            "http://github.com/mac-cain13/R.swift.Library",
            PACKAGE_KIND,
            new PackageState(null, "8998cfe77f4fce79ee6dfab0c88a7d551659d8fb", "5.4.0")
        );
        List<ResolvedPackage> resolvedPackages = createResolvedPackagesForFormat(testedFormat, rSwiftLibraryPackage);

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(new GitUrlParser());
        DependencyGraph dependencyGraph = transformer.transform(resolvedPackages);

        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);
        ExternalId rSwiftLibrary = ExternalId.FACTORY.createNameVersionExternalId(Forge.GITHUB, "mac-cain13/R.swift.Library", "5.4.0");
        graphAssert.hasRootDependency(rSwiftLibrary);
        graphAssert.hasRootSize(1);
    }

    @ParameterizedTest
    @MethodSource("knownFileFormats")
    void noGitExtensionTest(PackageResolvedFormat testedFormat) {
        ResolvedPackage swiftLogPackage = ResolvedPackage.version2(
            "swift-log",
            "http://github.com/apple/swift-log", // Won't be malformed for sanity check
            PACKAGE_KIND,
            new PackageState(null, "5d66f7ba25daf4f94100e7022febf3c75e37a6c7", "1.4.2")
        );
        List<ResolvedPackage> resolvedPackages = createResolvedPackagesForFormat(testedFormat, swiftLogPackage);

        PackageResolvedTransformer transformer = new PackageResolvedTransformer(new GitUrlParser());
        DependencyGraph dependencyGraph = transformer.transform(resolvedPackages);

        GraphAssert graphAssert = new GraphAssert(Forge.GITHUB, dependencyGraph);
        ExternalId swiftLog = ExternalId.FACTORY.createNameVersionExternalId(Forge.GITHUB, "apple/swift-log", "1.4.2");
        graphAssert.hasRootDependency(swiftLog);
        graphAssert.hasRootSize(1);
    }

    static Stream<PackageResolvedFormat> knownFileFormats() {
        return Arrays.stream(PackageResolvedFormatChecker.getKnownFileFormatVersions());
    }

    // Down-converts V2 to V1 if needed. Expects V2 packages
    private List<ResolvedPackage> createResolvedPackagesForFormat(PackageResolvedFormat packageResolvedFormat, ResolvedPackage... resolvedPackages) {
        if (V_1.equals(packageResolvedFormat)) {
            return Arrays.stream(resolvedPackages)
                .map(resolvedPackage -> ResolvedPackage.version1(resolvedPackage.getIdentity(), resolvedPackage.getLocation(), resolvedPackage.getPackageState()))
                .collect(Collectors.toList());
        } else if (V_2.equals(packageResolvedFormat)) {
            return Arrays.asList(resolvedPackages);
        } else {
            throw new UnsupportedOperationException("Only the V1 and V2 formats are tests. Restructure tests to take a new format version into account.");
        }
    }
}