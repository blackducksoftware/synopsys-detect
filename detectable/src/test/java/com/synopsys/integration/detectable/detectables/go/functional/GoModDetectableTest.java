package com.synopsys.integration.detectable.detectables.go.functional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.inspector.go.GoResolver;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class GoModDetectableTest extends DetectableFunctionalTest {

    public GoModDetectableTest() throws IOException {
        super("gomod");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("go.mod"));

        ExecutableOutput goListOutput = createStandardOutput(
            "github.com/gomods/athens",
            "github.com/sirupsen/logrus"
        );
        addExecutableOutput(goListOutput, "go", "list", "-m");

        ExecutableOutput goVersionOutput = createStandardOutput(
            "go version go1.14.5 darwin/amd64"
        );
        addExecutableOutput(goVersionOutput, "go", "version");

        ExecutableOutput goListUJsonOutput = createStandardOutput(
            "{",
            "\t\"Path\": \"github.com/codegangsta/negroni\",",
            "\t\"Version\": \"v1.0.0\"",
            "}",
            "",
            "{",
            "\t\"Path\": \"github.com/sirupsen/logrus\",",
            "\t\"Version\": \"v1.1.1\",",
            "\t\"Replace\": {",
            "\t\t\"Path\": \"github.com/sirupsen/logrus\",",
            "\t\t\"Version\": \"v2.0.0\"",
            "\t}",
            "}",
            "",
            "{",
            "\t\"Path\": \"github.com/davecgh/go-spew\",",
            "\t\"Version\": \"v1.1.1\"",
            "}"
        );
        addExecutableOutput(goListUJsonOutput, "go", "list", "-mod=readonly", "-m", "-u", "-json", "all");

        ExecutableOutput goModGraphOutput = createStandardOutput(
            "github.com/gomods/athens github.com/codegangsta/negroni@v1.0.0",
            "github.com/gomods/athens github.com/sirupsen/logrus@v1.1.1",
            "github.com/sirupsen/logrus@v1.1.1 github.com/davecgh/go-spew@v1.1.1"
        );
        addExecutableOutput(goModGraphOutput, "go", "mod", "graph");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull final DetectableEnvironment detectableEnvironment) {
        class GoResolverTest implements GoResolver {
            @Override
            public File resolveGo() throws DetectableException {
                return new File("go");
            }
        }
        return detectableFactory.createGoModCliDetectable(detectableEnvironment, new GoResolverTest());
    }

    @Override
    public void assertExtraction(@NotNull final Extraction extraction) {
        Assertions.assertEquals(2, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("github.com/codegangsta/negroni", "v1.0.0");
        graphAssert.hasRootDependency("github.com/sirupsen/logrus", "v2.0.0");
        graphAssert.hasParentChildRelationship("github.com/sirupsen/logrus", "v2.0.0", "github.com/davecgh/go-spew", "v1.1.1");
    }
}
