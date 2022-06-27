package com.synopsys.integration.detectable.detectables.swift.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class SwiftDetectableTest extends DetectableFunctionalTest {

    public SwiftDetectableTest() throws IOException {
        super("swift");
    }

    @Override
    protected void setup() throws IOException {

        addFile(Paths.get("Package.swift"));

        ExecutableOutput rootSwiftPackage = createStandardOutput(
            "Fetching https://github.com/apple/example-package-fisheryates.git",
            "Completed resolution in 1.03s",
            "Cloning https://github.com/apple/example-package-fisheryates.git",
            "Resolving https://github.com/apple/example-package-fisheryates.git at 2.0.5",
            "{",
            "   \"name\": \"DeckOfPlayingCards\",",
            "   \"url\": \"/Users/jakem/bazelWorkspace/sleuthifer/swift/example-package-deckofplayingcards\",",
            "   \"version\": \"unspecified\",",
            "   \"path\": \"/Users/jakem/bazelWorkspace/sleuthifer/swift/example-package-deckofplayingcards\",",
            "   \"dependencies\": [",
            "       {",
            "           \"name\": \"FisherYates\",",
            "           \"url\": \"https://github.com/apple/example-package-fisheryates.git\",",
            "           \"version\": \"2.0.5\",",
            "           \"path\": \"/Users/jakem/bazelWorkspace/sleuthifer/swift/example-package-deckofplayingcards/.build/checkouts/example-package-fisheryates\",",
            "           \"dependencies\": []",
            "       },",
            "       {",
            "           \"name\": \"PlayingCard\",",
            "           \"url\": \"https://github.com/apple/example-package-playingcard.git\",",
            "           \"version\": \"3.0.5\",",
            "           \"path\": \"/Users/jakem/bazelWorkspace/sleuthifer/swift/example-package-deckofplayingcards/.build/checkouts/example-package-playingcard\",",
            "           \"dependencies\": [",
            "               {",
            "                   \"name\": \"GenericLibrary\",",
            "                   \"url\": \"https://github.com/apple/example-package-genericLibrary.git\",",
            "                   \"version\": \"1.0.1\",",
            "                   \"path\": \"/Users/jakem/bazelWorkspace/sleuthifer/swift/example-package-deckofplayingcards/.build/checkouts/example-package-genericLibrary\",",
            "                   \"dependencies\": []",
            "               }",
            "           ]",
            "       }",
            "   ]",
            "}"
        );
        addExecutableOutput(rootSwiftPackage, "swift", "package", "show-dependencies", "--format", "json");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createSwiftCliDetectable(detectableEnvironment, () -> ExecutableTarget.forCommand("swift"));
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertNotEquals(0, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.COCOAPODS, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("FisherYates", "2.0.5");
        graphAssert.hasRootDependency("PlayingCard", "3.0.5");
        graphAssert.hasParentChildRelationship("PlayingCard", "3.0.5", "GenericLibrary", "1.0.1");
    }
}
