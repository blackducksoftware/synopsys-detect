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

public class GoModMinusWhyDetectableTest extends DetectableFunctionalTest {
    public GoModMinusWhyDetectableTest() throws IOException {
        super("gomod");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("go.mod"));

        ExecutableOutput goListOutput = createStandardOutput(
            "github.com/dgrijalva/jwt-go"
        );
        addExecutableOutput(goListOutput, "go", "list", "-m");

        ExecutableOutput goVersionOutput = createStandardOutput(
            "go version go1.14.5 darwin/amd64"
        );
        addExecutableOutput(goVersionOutput, "go", "version");

        ExecutableOutput goListUJsonOutput = createStandardOutput(
            "{\n",
            "\t\"Path\": \"github.com/dgrijalva/jwt-go\",\n",
            "\t\"Version\": \"v3.2.0\"\n",
            "}"
        );
        addExecutableOutput(goListUJsonOutput, "go", "list", "-mod=readonly", "-m", "-u", "-json", "all");

        ExecutableOutput goModGraphOutput = createStandardOutput(
            "github.com/dgrijalva/jwt-go github.com/dgrijalva/jwt-go@v3.2.0+incompatible"
        );
        addExecutableOutput(goModGraphOutput, "go", "mod", "graph");

        ExecutableOutput goModWhyOutput = createStandardOutput(
            "# github.com/dgrijalva/jwt-go",
            "(main module does not need module github.com/dgrijalva/jwt-go)",
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
        GoModDetectableOptions goModDetectableOptions = new GoModDetectableOptions(false);
        return detectableFactory.createGoModCliDetectable(detectableEnvironment, new GoResolverTest(), goModDetectableOptions);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(1, "Dependency verification being disabled should allow for dependencies to be found.");
        graphAssert.hasNoDependency("github.com/dgrijalva/jwt-go", "v3.2.0+incompatible");
        graphAssert.hasDependency("github.com/dgrijalva/jwt-go", "v3.2.0");
    }

}
