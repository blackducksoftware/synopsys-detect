package com.synopsys.integration.detectable.detectables.pip.inspector.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockDetectableOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

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
