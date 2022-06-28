package com.synopsys.integration.detectable.detectables.cargo.functional;

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

public class CargoLockDetectableTest extends DetectableFunctionalTest {

    public CargoLockDetectableTest() throws IOException {
        super("cargo");
    }

    @Override
    protected void setup() throws IOException {
        addFile(
            Paths.get("Cargo.toml"),
            "[package]",
            "name        = \"cargo-audit\"",
            "version     = \"0.12.0\""
        );

        addFile(
            Paths.get("Cargo.lock"),
            "[[package]]",
            "name = \"abscissa_core\"",
            "version = \"0.5.2\"",
            "dependencies = [",
            " \"abscissa_derive 0.5.0 (registry+https://github.com/rust-lang/crates.io-index)\",",
            " \"backtrace\"",
            "]",
            "",
            "[[package]]",
            "name = \"abscissa_derive\"",
            "version = \"0.5.0\"",
            "dependencies = [",
            " \"darling 0.10.2 (registry+https://github.com/rust-lang/crates.io-index)\"",
            "]",
            "",
            "[[package]]",
            "name = \"backtrace\"",
            "version = \"0.3.46\"",
            "",
            "[[package]]",
            "name = \"darling\"",
            "version = \"0.10.2\""
        );
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment detectableEnvironment) {
        return detectableFactory.createCargoDetectable(detectableEnvironment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        Assertions.assertEquals(1, extraction.getCodeLocations().size());

        Assertions.assertEquals("cargo-audit", extraction.getProjectName());
        Assertions.assertEquals("0.12.0", extraction.getProjectVersion());

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, extraction.getCodeLocations().get(0).getDependencyGraph());
        graphAssert.hasRootDependency("abscissa_core", "0.5.2");
        graphAssert.hasParentChildRelationship("abscissa_core", "0.5.2", "abscissa_derive", "0.5.0");
        graphAssert.hasParentChildRelationship("abscissa_core", "0.5.2", "backtrace", "0.3.46");
        graphAssert.hasParentChildRelationship("abscissa_derive", "0.5.0", "darling", "0.10.2");

        graphAssert.hasRootDependency("abscissa_derive", "0.5.0");
        graphAssert.hasRootDependency("backtrace", "0.3.46");
        graphAssert.hasRootDependency("darling", "0.10.2");

        graphAssert.hasRootSize(4);
    }
}
