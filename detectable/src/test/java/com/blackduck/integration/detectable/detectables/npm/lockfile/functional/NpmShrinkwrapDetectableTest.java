package com.blackduck.integration.detectable.detectables.npm.lockfile.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.functional.DetectableFunctionalTest;
import com.blackduck.integration.detectable.util.graph.NameVersionGraphAssert;

public class NpmShrinkwrapDetectableTest extends DetectableFunctionalTest {

    public NpmShrinkwrapDetectableTest() throws IOException {
        super("npmShrinkwrap");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("npm-shrinkwrap.json"),
            "{",
            "   \"name\": \"fec-builder\",",
            "   \"version\": \"1.3.7\",",
            "   \"lockfileVersion\": 3,",
            "   \"requires\": true,",
            "   \"packages\": {",
            "       \"node_modules/abbrev\": {",
            "           \"version\": \"1.0.9\"",
            "       },",
            "       \"node_modules/accepts\": {",
            "           \"version\": \"1.3.3\",",
            "           \"dependencies\": {",
            "               \"mime-types\": \"2.1.14\",",
            "               \"negotiator\": \"0.6.1\"",
            "           }",
            "       },",
            "        \"node_modules/mime-types\": {",
            "           \"version\": \"https://registry.npmjs.org/mime-types/-/mime-types-2.1.13.tgz\",",
            "           \"integrity\": \"sha1-4HqqnGxrmnyjASxpADrSWjnpKog=\",",
            "           \"dependencies\": {",
            "               \"mime-db\": \"https://registry.npmjs.org/mime-db/-/mime-db-1.25.0.tgz\"",
            "           }",
            "       },",
            "       \"node_modules/mime-db\": {",
            "           \"version\": \"https://registry.npmjs.org/mime-db/-/mime-db-1.25.0.tgz\",",
            "           \"integrity\": \"sha1-wY29fHOl2/b0SgJNwNFloeexw5I=\"",
            "       },",
            "       \"node_modules/negotiator\": {",
            "           \"version\": \"https://registry.npmjs.org/negotiator/-/negotiator-0.6.1.tgz\",",
            "           \"integrity\": \"sha1-wY29fHOl2/b0SgJNwNFloeexw5I=\"",
            "       },",
            "       \"node_modules/peer-example\": {",
            "           \"version\": \"1.0.0\",",
            "           \"integrity\": \"sha1-wY36fHOl2/b0SgJNwNFloeexw57=\"",
            "       }",
            "   }",
            "}"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createNpmShrinkwrapDetectable(detectableEnvironment, new NpmLockfileOptions(EnumListFilter.excludeNone()));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size(), "A code location should have been generated.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, extraction.getCodeLocations().get(0).getDependencyGraph());

        graphAssert.hasRootSize(6);
        graphAssert.hasRootDependency("abbrev", "1.0.9");
        graphAssert.hasRootDependency("accepts", "1.3.3");
        graphAssert.hasRootDependency("mime-types", "https://registry.npmjs.org/mime-types/-/mime-types-2.1.13.tgz");
        graphAssert.hasRootDependency("mime-db", "https://registry.npmjs.org/mime-db/-/mime-db-1.25.0.tgz");
        graphAssert.hasRootDependency("negotiator", "https://registry.npmjs.org/negotiator/-/negotiator-0.6.1.tgz");
        graphAssert.hasRootDependency("peer-example", "1.0.0");
        graphAssert.hasParentChildRelationship("accepts", "1.3.3", "mime-types", "https://registry.npmjs.org/mime-types/-/mime-types-2.1.13.tgz");
        graphAssert.hasParentChildRelationship(
            "mime-types",
            "https://registry.npmjs.org/mime-types/-/mime-types-2.1.13.tgz",
            "mime-db",
            "https://registry.npmjs.org/mime-db/-/mime-db-1.25.0.tgz"
        );
        graphAssert.hasParentChildRelationship("accepts", "1.3.3", "negotiator", "https://registry.npmjs.org/negotiator/-/negotiator-0.6.1.tgz");

    }
}
