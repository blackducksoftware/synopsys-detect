package com.blackduck.integration.detectable.detectables.go.vendr;

import java.io.IOException;
import java.nio.file.Paths;

import com.blackduck.integration.detectable.functional.DetectableFunctionalTest;
import com.blackduck.integration.detectable.util.graph.NameVersionGraphAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.extraction.Extraction;

public class GoVndrDetectableTest extends DetectableFunctionalTest {
    public GoVndrDetectableTest() throws IOException {
        super("vndr");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("vendor.conf"),
            "github.com/klauspost/compress v1.4.1",
            "github.com/klauspost/cpuid v1.2.0",
            "github.com/klauspost/pgzip v1.2.1",

            // Comments
            "github.com/Azure/go-ansiterm d6e3b3328b783f23731bc4d058875b0371ff8109 # v0.4.15",
            "# github.com/Azure/go-ansiterm dont_include_me",

            // Multiple spaces (IDETECT-2722)
            "github.com/Microsoft/go-winio   5b44b70ab3ab4d291a7c1d28afe7b4afeced0ed4"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createGoVndrDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(5);
        graphAssert.hasRootDependency("github.com/klauspost/compress", "v1.4.1");
        graphAssert.hasRootDependency("github.com/klauspost/cpuid", "v1.2.0");
        graphAssert.hasRootDependency("github.com/klauspost/pgzip", "v1.2.1");
        graphAssert.hasRootDependency("github.com/Azure/go-ansiterm", "d6e3b3328b783f23731bc4d058875b0371ff8109");
        graphAssert.hasRootDependency("github.com/Microsoft/go-winio", "5b44b70ab3ab4d291a7c1d28afe7b4afeced0ed4");

        graphAssert.hasNoDependency("github.com/Azure/go-ansiterm", "dont_include_me");
    }
}
