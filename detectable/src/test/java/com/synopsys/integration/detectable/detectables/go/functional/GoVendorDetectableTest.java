package com.synopsys.integration.detectable.detectables.go.functional;

import java.io.IOException;
import java.nio.file.Paths;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class GoVendorDetectableTest extends DetectableFunctionalTest {

    public GoVendorDetectableTest() throws IOException {
        super("vendor");
    }

    @Override
    protected void setup() throws IOException {
        addDirectory(Paths.get("vendor"));

        addFile(
            Paths.get("vendor/vendor.json"),
            "{",
            "   \"comment\": \"\",",
            "   \"ignore\": \"test\",",
            "   \"package\": [",
            "       {",
            "           \"checksumSHA1\": \"DTy0iJ2w5C+FDsN9EnzfhNmvS+o=\",",
            "           \"path\": \"github.com/pkg/errors\",",
            "           \"revision\": \"059132a15dd08d6704c67711dae0cf35ab991756\"",
            "       },",
            "       {",
            "           \"checksumSHA1\": \"Ykp1hHqP+5CeV/MymOaxS2zblb4=\",",
            "           \"path\": \"github.com/pkg/math\",",
            "           \"revision\": \"f2ed9e40e245cdeec72c4b642d27ed4553f90667\"",
            "       }",
            "   ],",
            "   \"rootPath\": \"synopsys.com/integration/hello\"",
            "}"
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createGoVendorDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GOLANG, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("github.com/pkg/errors", "059132a15dd08d6704c67711dae0cf35ab991756");
        graphAssert.hasRootDependency("github.com/pkg/math", "f2ed9e40e245cdeec72c4b642d27ed4553f90667");
    }
}
