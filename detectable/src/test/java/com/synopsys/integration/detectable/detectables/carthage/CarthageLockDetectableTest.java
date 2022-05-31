package com.synopsys.integration.detectable.detectables.carthage;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class CarthageLockDetectableTest extends DetectableFunctionalTest {
    public CarthageLockDetectableTest() throws IOException {
        super("Carthage");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("Cartfile.resolved"),
            "binary \"https://downloads.localytics.com/SDKs/iOS/Localytics.json\" \"6.2.1\"",
            "github \"GEOSwift/GEOSwift\" \"8.0.2\"",
            "github \"MobileNativeFoundation/Kronos\" \"4.2.1\"",
            "github \"ReactiveCocoa/ReactiveCocoa\" \"11.1.0\"",
            "github \"realm/realm-cocoa\" \"v10.7.2\""
        );
    }

    @Override
    @NotNull
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createCarthageDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GITHUB, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootDependency("GEOSwift/GEOSwift", "8.0.2");
        graphAssert.hasRootDependency("MobileNativeFoundation/Kronos", "4.2.1");
        graphAssert.hasRootDependency("ReactiveCocoa/ReactiveCocoa", "11.1.0");
        graphAssert.hasRootDependency("realm/realm-cocoa", "v10.7.2");
        graphAssert.hasNoDependency("https://downloads.localytics.com/SDKs/iOS/Localytics.json", "6.2.1");
        graphAssert.hasRootSize(4);
    }
}
