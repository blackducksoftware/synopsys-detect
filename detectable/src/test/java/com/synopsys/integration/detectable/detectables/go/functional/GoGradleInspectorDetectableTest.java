package com.synopsys.integration.detectable.detectables.go.functional;

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

public class GoGradleInspectorDetectableTest extends DetectableFunctionalTest {
    public GoGradleInspectorDetectableTest() throws IOException {
        super("gogradle");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("gogradle.lock"),
            "apiVersion: \"0.11.4\"",
            "dependencies:",
            "  build:",
            "  - urls:",
            "    - \"https://github.com/golang/example.git\"",
            "    - \"git@github.com:golang/example.git\"",
            "    vcs: \"git\"",
            "    name: \"github.com/golang/example\"",
            "    commit: \"0dea2d0bf90754ffa40e0cb2f23b638f3e3d7e09\"",
            "    transitive: false",
            "  - vcs: \"git\"",
            "    name: \"golang.org/x/crypto\"",
            "    commit: \"9756ffdc24725223350eb3266ffb92590d28f278\"",
            "    url: \"https://go.googlesource.com/crypto\"",
            "    transitive: false",
            "  test: []"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createGoGradleDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("github.com/golang/example", "0dea2d0bf90754ffa40e0cb2f23b638f3e3d7e09");
        graphAssert.hasRootDependency("crypto", "9756ffdc24725223350eb3266ffb92590d28f278");
    }

}
