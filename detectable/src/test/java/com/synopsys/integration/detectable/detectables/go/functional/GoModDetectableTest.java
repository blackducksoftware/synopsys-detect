package com.synopsys.integration.detectable.detectables.go.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GoResolver;
import com.synopsys.integration.detectable.detectables.go.GoModDetectableOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class GoModDetectableTest extends DetectableFunctionalTest {
    public GoModDetectableTest() throws IOException {
        super("gomod");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("go.mod"));

        ExecutableOutput goListOutput = createStandardOutput(
            "github.com/gomods/athens",
            "github.com/sirupsen/logrus",
            "github.com/dgrijalva/jwt-go"
        );
        addExecutableOutput(goListOutput, "go", "list", "-m");

        ExecutableOutput goVersionOutput = createStandardOutput(
            "go version go1.14.5 darwin/amd64"
        );
        addExecutableOutput(goVersionOutput, "go", "version");

        ExecutableOutput goListUJsonOutput = createStandardOutput(
            "{\n",
            "\t\"Path\": \"github.com/codegangsta/negroni\",\n",
            "\t\"Version\": \"v1.0.0\"\n",
            "}\n",
            "",
            "{\n",
            "\t\"Path\": \"github.com/sirupsen/logrus\",\n",
            "\t\"Version\": \"v1.1.1\",\n",
            "\t\"Replace\": {\n",
            "\t\t\"Path\": \"github.com/sirupsen/logrus\",\n",
            "\t\t\"Version\": \"v2.0.0\"\n",
            "\t}\n",
            "}\n",
            "",
            "{\n",
            "\t\"Path\": \"github.com/davecgh/go-spew\",\n",
            "\t\"Version\": \"v1.1.1\"\n",
            "}",
            "{\n",
            "\t\"Path\": \"github.com/dgrijalva/jwt-go\",\n",
            "\t\"Version\": \"v3.2.0\"\n",
            "}"
        );
        addExecutableOutput(goListUJsonOutput, "go", "list", "-mod=readonly", "-m", "-u", "-json", "all");

        ExecutableOutput goModGraphOutput = createStandardOutput(
            "github.com/gomods/athens github.com/codegangsta/negroni@v1.0.0",
            "github.com/gomods/athens github.com/sirupsen/logrus@v1.1.1",
            "github.com/sirupsen/logrus@v1.1.1 github.com/davecgh/go-spew@v1.1.1",
            "github.com/dgrijalva/jwt-go github.com/dgrijalva/jwt-go@v3.2.0+incompatible"
        );
        addExecutableOutput(goModGraphOutput, "go", "mod", "graph");

        ExecutableOutput goModWhyOutput = createStandardOutput(
            "# github.com/gomods/athens",
            "github.com/codegangsta/negroni",
            "github.com/sirupsen/logrus",
            "",
            "# github.com/sirupsen/logrus",
            "github.com/davecgh/go-spew",
            "# github.com/dgrijalva/jwt-go",
            "github.com/davecgh/go-spew"
        );

        addExecutableOutput(goModWhyOutput, "go", "mod", "why", "-m", "all");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        class GoResolverTest implements GoResolver {
            @Override
            public ExecutableTarget resolveGo() throws DetectableException {
                return ExecutableTarget.forCommand("go");
            }
        }
        GoModDetectableOptions goModDetectableOptions = new GoModDetectableOptions(true);
        return detectableFactory.createGoModCliDetectable(detectableEnvironment, new GoResolverTest(), goModDetectableOptions);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(3, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("github.com/codegangsta/negroni", "v1.0.0");
        graphAssert.hasRootDependency("github.com/sirupsen/logrus", "v2.0.0");
        graphAssert.hasParentChildRelationship("github.com/sirupsen/logrus", "v2.0.0", "github.com/davecgh/go-spew", "v1.1.1");
        graphAssert.hasNoDependency("github.com/dgrijalva/jwt-go", "v3.2.0+incompatible");
        graphAssert.hasDependency("github.com/dgrijalva/jwt-go", "v3.2.0");
    }

}
