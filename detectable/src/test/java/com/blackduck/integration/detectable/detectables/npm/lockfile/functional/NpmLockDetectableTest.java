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

public class NpmLockDetectableTest extends DetectableFunctionalTest {

    public NpmLockDetectableTest() throws IOException {
        super("npmLock");
    }

    @Override
    public void setup() throws IOException {
        addFile(
            Paths.get("package-lock.json"),
            "{",
            "   \"name\": \"knockout-tournament\",",
            "   \"version\": \"1.0.0\",",
            "   \"lockfileVersion\": 3,",
            "   \"requires\": true,",
            "   \"packages\": {",
            "       \"node_modules/balanced-match\": {",
            "           \"version\": \"1.0.0\",",
            "           \"resolved\": \"https://registry.npmjs.org/balanced-match/-/balanced-match-1.0.0.tgz\",",
            "           \"integrity\": \"sha1-ibTRmasr7kneFk6gK4nORi1xt2c=\",",
            "           \"dev\": true",
            "       },",
            "       \"node_modules/brace-expansion\": {",
            "           \"version\": \"1.1.8\",",
            "           \"resolved\": \"https://registry.npmjs.org/brace-expansion/-/brace-expansion-1.1.8.tgz\",",
            "           \"integrity\": \"sha1-wHshHHyVLsH479Uad+8NHTmQopI=\",",
            "           \"dev\": true,",
            "           \"dependencies\": {",
            "               \"balanced-match\": \"1.0.0\",",
            "               \"concat-map\": \"0.0.1\"",
            "           }",
            "       },",
            "       \"node_modules/concat-map\": {",
            "           \"version\": \"0.0.1\",",
            "           \"resolved\": \"https://registry.npmjs.org/concat-map/-/concat-map-0.0.1.tgz\",",
            "           \"integrity\": \"sha1-2Klr13/Wjfd5OnMDajug1UBdR3s=\",",
            "           \"dev\": true",
            "       },",
            "       \"node_modules/peer-example\": {",
            "           \"version\": \"1.0.0\",",
            "           \"resolved\": \"https://synopsys.com/404/peer-example.tgz\",",
            "           \"integrity\": \"sha1-1Klr13/Wjfd5OnMDajug1UBdR3s=\",",
            "           \"peer\": true",
            "       }",
            "   }",
            "}"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        return detectableFactory.createNpmPackageLockDetectable(environment, new NpmLockfileOptions(EnumListFilter.excludeNone()));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size(), "A code location should have been generated.");

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.NPMJS, extraction.getCodeLocations().get(0).getDependencyGraph());

        graphAssert.hasRootSize(4);
        graphAssert.hasRootDependency("peer-example", "1.0.0");
        graphAssert.hasRootDependency("brace-expansion", "1.1.8");
        graphAssert.hasRootDependency("balanced-match", "1.0.0");
        graphAssert.hasRootDependency("concat-map", "0.0.1");
        graphAssert.hasParentChildRelationship("brace-expansion", "1.1.8", "balanced-match", "1.0.0");
    }

}
