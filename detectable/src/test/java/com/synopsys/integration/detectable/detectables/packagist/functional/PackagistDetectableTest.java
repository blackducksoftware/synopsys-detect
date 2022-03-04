package com.synopsys.integration.detectable.detectables.packagist.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectableOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PackagistDetectableTest extends DetectableFunctionalTest {

    public PackagistDetectableTest() throws IOException {
        super("composer");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("composer.lock"),
            "{",
            "   \"_readme\": [\"dummy README\"],",
            "   \"hash\": \"3bedbf455f54168900e467a64bbe2b86\",",
            "   \"packages\": [",
            "       {",
            "           \"name\": \"clue/graph\",",
            "           \"version\": \"v0.9.0\",",
            "           \"require\": {",
            "               \"php\": \">=5.3.0\"",
            "           }",
            "       },",
            "       {",
            "           \"name\": \"graphp/algorithms\",",
            "           \"version\": \"v0.8.1\",",
            "           \"require\": {",
            "               \"clue/graph\": \"~0.9.0|~0.8.0\",",
            "               \"php\": \">=5.3\"",
            "           }",
            "       },",
            "       {",
            "           \"name\": \"graphp/graphviz\",",
            "           \"version\": \"v0.2.1\",",
            "           \"require\": {",
            "               \"clue/graph\": \"~0.9.0|~0.8.0\",",
            "               \"graphp/algorithms\": \"~0.8.0\",",
            "               \"php\": \">=5.3.0\"",
            "           }",
            "       }",
            "   ],",
            "   \"packages-dev\": []",
            "}"
        );

        addFile(
            Paths.get("composer.json"),
            "{",
            "   \"name\": \"clue/graph-composer\",",
            "   \"version\": \"1.0.0\",",
            "   \"keywords\": [\"dependency graph\", \"visualize dependencies\", \"visualize composer\"],",
            "   \"license\": \"MIT\",",
            "   \"require\": {",
            "       \"php\": \"^5.3.6 || ^7.0\",",
            "       \"clue/graph\": \"^0.9.0\",",
            "       \"graphp/graphviz\": \"^0.2.0\"",
            "   }",
            "}"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createComposerDetectable(detectableEnvironment, new ComposerLockDetectableOptions(EnumListFilter.excludeNone()));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PACKAGIST, extraction.getCodeLocations().get(0).getDependencyGraph());

        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("clue/graph", "v0.9.0");
        graphAssert.hasRootDependency("graphp/graphviz", "v0.2.1");
        graphAssert.hasParentChildRelationship("graphp/algorithms", "v0.8.1", "clue/graph", "v0.9.0");
        graphAssert.hasParentChildRelationship("graphp/graphviz", "v0.2.1", "graphp/algorithms", "v0.8.1");
        graphAssert.hasParentChildRelationship("graphp/graphviz", "v0.2.1", "clue/graph", "v0.9.0");
    }
}
