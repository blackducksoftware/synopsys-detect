package com.synopsys.integration.detectable.detectables.cargo.unit;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLock;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoLockTransformer;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class CargoLockTransformerTest {

    public CargoLock cargoLock(String... lines) {
        return new Toml().read(String.join(System.lineSeparator(), Arrays.asList(lines))).to(CargoLock.class);
    }

    @Test
    public void testParsesNamesAndVersionsSimple() throws DetectableException {
        CargoLock input = cargoLock(
            "[[package]]",
            "name = \"test1\"", "version = \"1.0.0\"",
            "",
            "[[package]]",
            "name = \"test2\"",
            "version = \"2.0.0\""
        );
        CargoLockTransformer cargoLockTransformer = new CargoLockTransformer();
        Optional<DependencyGraph> graph = cargoLockTransformer.toDependencyGraph(input);

        Assertions.assertTrue(graph.isPresent());
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph.get());
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("test1", "1.0.0");
        graphAssert.hasRootDependency("test2", "2.0.0");
    }

    @Test
    public void testParsesNoisyDependencyLines() throws DetectableException {
        CargoLock input = cargoLock(
            "[[package]]",
            "name = \"test1\"",
            "version = \"1.0.0\"",
            "dependencies = [",
            "\"dep1 0.5.0 (registry+https://github.com/rust-lang/crates.io-index)\"",
            "\"dep2 2.0.0 287486429\"",
            "]",
            "",
            "[[package]]",
            "name = \"dep1\"",
            "version = \"0.5.0\"",
            "",
            "[[package]]",
            "name = \"dep2\"",
            "version = \"2.0.0\""
        );
        CargoLockTransformer cargoLockTransformer = new CargoLockTransformer();
        Optional<DependencyGraph> graph = cargoLockTransformer.toDependencyGraph(input);

        Assertions.assertTrue(graph.isPresent());
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph.get());
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("test1", "1.0.0");
        graphAssert.hasParentChildRelationship("test1", "1.0.0", "dep1", "0.5.0");
        graphAssert.hasParentChildRelationship("test1", "1.0.0", "dep2", "2.0.0");
    }

    @Test
    public void testCorrectNumberOfRootDependencies() throws DetectableException {
        CargoLock input = cargoLock(
            "[[package]]",
            "name = \"test1\"",
            "version = \"1.0.0\"",
            "dependencies = [",
            "\"dep1\",",
            "\"dep2\"",
            "]",
            "",
            "[[package]]",
            "name = \"dep1\"",
            "version = \"0.5.0\"",
            "dependencies = [",
            "\"dep2\"",
            "]",
            "",
            "[[package]]",
            "name = \"dep2\"",
            "version = \"0.6.0\""
        );
        CargoLockTransformer cargoLockTransformer = new CargoLockTransformer();
        Optional<DependencyGraph> graph = cargoLockTransformer.toDependencyGraph(input);

        Assertions.assertTrue(graph.isPresent());
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph.get());
        graphAssert.hasRootSize(1);
    }

    @Test
    public void testCatchInvalidSyntaxInLockFile() {
        CargoLock input = cargoLock(
            "[[package]]",
            "name \"test1\"",
            "version \"test2\""
        );
        CargoLockTransformer cargoLockTransformer = new CargoLockTransformer();
        Assertions.assertThrows(DetectableException.class, () -> cargoLockTransformer.toDependencyGraph(input));

    }
}
