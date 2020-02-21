package com.synopsys.integration.detectable.detectables.npm.lockfile.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class NpmShrinkwrapDetectableTest extends DetectableFunctionalTest {

    public NpmShrinkwrapDetectableTest() throws IOException {
        super("npmShrinkwrap");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("npm-shrinkwrap.json"),
            "{",
            "   \"name\": \"fec-builder\",",
            "   \"version\": \"1.3.7\",",
            "   \"lockfileVersion\": 1,",
            "   \"requires\": true,",
            "   \"dependencies\": {",
            "       \"abbrev\": {",
            "           \"version\": \"1.0.9\"",
            "       },",
            "       \"accepts\": {",
            "           \"version\": \"1.3.3\",",
            "           \"requires\": {",
            "               \"mime-types\": \"2.1.14\",",
            "               \"negotiator\": \"0.6.1\"",
            "           }",
            "       },",
            "        \"mime-types\": {",
            "           \"version\": \"https://registry.npmjs.org/mime-types/-/mime-types-2.1.13.tgz\",",
            "           \"integrity\": \"sha1-4HqqnGxrmnyjASxpADrSWjnpKog=\",",
            "           \"requires\": {",
            "               \"mime-db\": \"https://registry.npmjs.org/mime-db/-/mime-db-1.25.0.tgz\"",
            "           }",
            "       }",
            "   }",
            "}"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createNpmShrinkwrapDetectable(detectableEnvironment, new NpmLockfileOptions(true));
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size(), "A code location should have been generated.");

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, extraction.getCodeLocations().get(0).getDependencyGraph());

        graphAssert.hasRootSize(3);
        graphAssert.hasRootDependency("abbrev", "1.0.9");
        graphAssert.hasRootDependency("accepts", "1.3.3");
        graphAssert.hasRootDependency("mime-types", "https://registry.npmjs.org/mime-types/-/mime-types-2.1.13.tgz");
        graphAssert.hasParentChildRelationship("accepts", "1.3.3", "mime-types", "https://registry.npmjs.org/mime-types/-/mime-types-2.1.13.tgz");

    }
}
