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

public class PodlockDetectableTest extends DetectableFunctionalTest {
    public PodlockDetectableTest() throws IOException {
        super("podlock");
    }

    @Override
    public void setup() throws IOException {

        addFile(
            Paths.get("Podfile.lock"),
            "PODS:",
            "  - Allihoopa-macOS (1.1.0):",
            "    - AllihoopaCore (~> 1.1.0)",
            "  - AllihoopaCore (1.1.0)",
            "  - Mapbox-macOS-SDK (0.4.1)",
            "  - RepliesSDK-macOS (0.2.22)",
            "",
            "DEPENDENCIES:",
            "  - Allihoopa-macOS (~> 1.1)",
            "  - Mapbox-macOS-SDK (~> 0.4)",
            "  - RepliesSDK-macOS (~> 0.2)",
            "",
            "SPEC CHECKSUMS:",
            "  Allihoopa-macOS: 5c2fb29ed9d4266bc3ba0b3ec3ef7d937cd856f6",
            "  AllihoopaCore: 78c8c6695856b376ae4c6bb2385bcd8de099dfbc",
            "  Mapbox-macOS-SDK: 942319f0f1567b7fc9f5bb452428331a4ba4990a",
            "  RepliesSDK-macOS: 42c5f6956a1cebb259e8334e21cb6d04e70813a7",
            "",
            "PODFILE CHECKSUM: cf96735ee6b7cfa92b1189e11efbd4406137e30f",
            "",
            "COCOAPODS: 1.2.0"
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
        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("Allihoopa-macOS", "1.1.0");
        graphAssert.hasRootDependency("Mapbox-macOS-SDK", "0.4.1");
        graphAssert.hasRootDependency("RepliesSDK-macOS", "0.2.22");

        graphAssert.hasParentChildRelationship("Allihoopa-macOS", "1.1.0", "AllihoopaCore", "1.1.0");
    }
}