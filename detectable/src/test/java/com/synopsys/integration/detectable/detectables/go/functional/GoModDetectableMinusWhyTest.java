package com.synopsys.integration.detectable.detectables.go.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GoResolver;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliDetectableOptions;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.executable.ExecutableOutput;

public class GoModDetectableMinusWhyTest extends DetectableFunctionalTest {
    public GoModDetectableMinusWhyTest() throws IOException {
        super("gomod");
    }

    @Override
    protected void setup() throws IOException {
        addFile(Paths.get("go.mod"));

        ExecutableOutput goListOutput = new ExecutableOutput(FunctionalTestFiles.asString("/go/go-list.xout"), StringUtils.EMPTY);
        addExecutableOutput(goListOutput, "go", "list", "-m", "-json");

        ExecutableOutput goVersionOutput = createStandardOutput(
            "go version go1.16.5 darwin/amd64"
        );
        addExecutableOutput(goVersionOutput, "go", "version");

        ExecutableOutput goListUJsonOutput = new ExecutableOutput(FunctionalTestFiles.asString("/go/go-list-all.xout"), StringUtils.EMPTY);
        addExecutableOutput(goListUJsonOutput, "go", "list", "-mod=readonly", "-m", "-u", "-json", "all");

        ExecutableOutput goModGraphOutput = createStandardOutput(
            "github.com/gin-gonic/gin golang.org/x/text@v0.3.0",
            "github.com/gin-gonic/gin sigs.k8s.io/yaml@v1.2.0",
            "golang.org/x/text@v0.3.0 golang.org/x/tools@v0.0.0-20180917221912-90fa682c2a6e",
            "gopkg.in/yaml.v2@v2.2.8 gopkg.in/check.v1@v0.0.0-20161208181325-20d25e280405",
            "sigs.k8s.io/yaml@v1.2.0 github.com/davecgh/go-spew@v1.1.1",
            "sigs.k8s.io/yaml@v1.2.0 gopkg.in/yaml.v2@v2.2.8"
        );
        addExecutableOutput(goModGraphOutput, "go", "mod", "graph");
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
        GoModCliDetectableOptions goModCliDetectableOptions = new GoModCliDetectableOptions(false);
        return detectableFactory.createGoModCliDetectable(detectableEnvironment, new GoResolverTest(), goModCliDetectableOptions);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        assertSuccessfulExtraction(extraction);
        assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);

        graphAssert.hasRootDependency("golang.org/x/text", "v0.3.6");
        // This version should be replaced with a v0.3.6
        graphAssert.hasNoDependency("golang.org/x/text", "v0.3.0");


        graphAssert.hasDependency("golang.org/x/tools", "v0.0.0-20180917221912-90fa682c2a6e");
        graphAssert.hasParentChildRelationship("golang.org/x/text", "v0.3.6", "golang.org/x/tools", "v0.0.0-20180917221912-90fa682c2a6e");

        graphAssert.hasRootDependency("sigs.k8s.io/yaml", "v1.2.0");
        graphAssert.hasDependency("github.com/davecgh/go-spew", "v1.1.1");
        graphAssert.hasParentChildRelationship("sigs.k8s.io/yaml", "v1.2.0", "github.com/davecgh/go-spew", "v1.1.1");
        graphAssert.hasDependency("gopkg.in/yaml.v2", "v2.2.8");
        graphAssert.hasParentChildRelationship("sigs.k8s.io/yaml", "v1.2.0", "gopkg.in/yaml.v2", "v2.2.8");
        graphAssert.hasDependency("gopkg.in/check.v1", "v0.0.0-20161208181325-20d25e280405");
        graphAssert.hasParentChildRelationship("gopkg.in/yaml.v2", "v2.2.8", "gopkg.in/check.v1", "v0.0.0-20161208181325-20d25e280405");
    }

}
