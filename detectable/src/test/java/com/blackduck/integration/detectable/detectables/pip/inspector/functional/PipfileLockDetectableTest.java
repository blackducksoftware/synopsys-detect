package com.blackduck.integration.detectable.detectables.pip.inspector.functional;

import java.io.IOException;
import java.nio.file.Paths;

import com.blackduck.integration.detectable.detectables.pipenv.parse.PipfileLockDetectableOptions;
import com.blackduck.integration.detectable.functional.DetectableFunctionalTest;
import com.blackduck.integration.detectable.util.graph.NameVersionGraphAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.extraction.Extraction;

public class PipfileLockDetectableTest extends DetectableFunctionalTest {
    protected PipfileLockDetectableTest() throws IOException {
        super("pipfile lock");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("Pipfile.lock"),
            "{",
            "   \"default\": {",
            "       \"asgiref\": {",
            "           \"hashes\": [",
            "               \"sha256:2f8abc20f7248433085eda803936d98992f1343ddb022065779f37c5da0181d0\"",
            "           ],",
            "           \"version\": \"==3.5.0\"",
            "       },",
            "       \"crispy-bootstrap5\": {",
            "           \"version\": \"==0.6\"",
            "       }",
            "   },",
            "   \"develop\": {",
            "       \"flake8\": {",
            "           \"version\": \"==4.0.1\"",
            "       }",
            "   }",
            "}"
        );
    }

    @Override
    public @NotNull Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createPipfileLockDetectable(detectableEnvironment, new PipfileLockDetectableOptions(EnumListFilter.excludeNone()));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("asgiref", "3.5.0");
        graphAssert.hasRootDependency("crispy-bootstrap5", "0.6");
        graphAssert.hasRootDependency("flake8", "4.0.1");
    }
}
