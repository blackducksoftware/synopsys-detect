package com.synopsys.integration.detectable.detectables.swift.lock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

class SwiftPackageResolvedV1DetectableTest extends DetectableFunctionalTest {
    protected SwiftPackageResolvedV1DetectableTest() throws IOException {
        super("SwiftPackageResolved");
    }

    @Override
    public void setup() throws IOException {
        addFile(Paths.get("Package.swift"));
        addFileFromResources(Paths.get("Package.resolved"), "/swift/v1/Package.resolved");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        return detectableFactory.createSwiftPackageResolvedDetectable(environment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        assertEquals(1, extraction.getCodeLocations().size(), "Expected only one code location.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GITHUB, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootDependency("apple/swift-argument-parser", "1.0.1");
        graphAssert.hasRootDependency("auth0/Auth0.swift", "1.35.0");
        graphAssert.hasRootDependency("mac-cain13/R.swift.Library", "5.4.0");

        // The GitUrlParser can handle this
        graphAssert.hasRootDependency("invalid/url", "1.2.3");

        graphAssert.hasRootSize(4);
    }
}