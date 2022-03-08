package com.synopsys.integration.detectable.detectables.pip.inspector.functional;

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

public class PoetryDetectableTest extends DetectableFunctionalTest {

    PoetryDetectableTest() throws IOException {
        super("poetry");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("pyproject.toml"),
            "[tool.poetry]"
        );

        addFile(
            Paths.get("poetry.lock"),
            "[[package]]",
            "category = \"dev\"",
            "name = \"appdirs\"",
            "python-versions = \"*\"",
            "version = \"1.4.3\"\n",
            "",
            "[[package]]",
            "category = \"dev\"",
            "name = \"atomicwrites\"",
            "python-versions = \">=2.7, !=3.0.*, !=3.1.*, !=3.2.*, !=3.3.*\"",
            "version = \"1.4.0\"",
            "",
            "[package.dependencies]",
            "importlib-metadata = \"*\"",
            "",
            "[[package]]",
            "category = \"dev\"",
            "name = \"importlib-metadata\"",
            "python-versions = \"!=3.0.*,!=3.1.*,!=3.2.*,!=3.3.*,!=3.4.*,>=2.7\"",
            "version = \"1.6.0\""
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createPoetryDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, extraction.getCodeLocations().get(0).getDependencyGraph());

        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("appdirs", "1.4.3");
        graphAssert.hasRootDependency("atomicwrites", "1.4.0");
        graphAssert.hasParentChildRelationship("atomicwrites", "1.4.0", "importlib-metadata", "1.6.0");
    }
}
