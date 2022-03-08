package com.synopsys.integration.detectable.detectables.cocoapods.functional;

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

public class PodlockTransitiveDetectableTest extends DetectableFunctionalTest {
    public PodlockTransitiveDetectableTest() throws IOException {
        super("podlock-transitive");
    }

    @Override
    public void setup() throws IOException {

        addFile(
            Paths.get("Podfile.lock"),
            "PODS:",
            "  - OktaDeviceSDK (0.0.1):",
            "    - JOSESwift (= 1.8.1)",
            "",
            "DEPENDENCIES:",
            "  - OktaDeviceSDK (from `https://github.com/okta/okta-devices-swift.git`, commit `89ee1a16ff37a17604c48fa277b7e86a0756bddf`)"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        return detectableFactory.createPodLockDetectable(environment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.COCOAPODS, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("OktaDeviceSDK", "0.0.1");
    }
}