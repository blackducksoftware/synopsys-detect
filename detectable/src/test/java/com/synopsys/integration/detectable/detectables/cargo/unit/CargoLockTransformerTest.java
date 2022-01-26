package com.synopsys.integration.detectable.detectables.cargo.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.data.CargoLock;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLockPackage;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoDependencyLineParser;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoLockDataTransformer;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoLockTransformer;
import com.synopsys.integration.detectable.util.CycleDetectedException;
import com.synopsys.integration.detectable.util.NameOptionalVersion;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.util.NameVersion;

public class CargoLockTransformerTest {

    public List<CargoLockPackage> cargoLock(String... lines) {
        CargoLock cargoLock = new Toml().read(String.join(System.lineSeparator(), Arrays.asList(lines))).to(CargoLock.class);
        CargoDependencyLineParser cargoDependencyLineParser = new CargoDependencyLineParser();
        CargoLockDataTransformer cargoLockDataTransformer = new CargoLockDataTransformer(cargoDependencyLineParser);
        return cargoLock.getPackages()
            .map(packages -> packages.stream()
                .map(cargoLockDataTransformer::transform)
                .collect(Collectors.toList()))
            .orElseThrow(() -> new RuntimeException("Test data shouldn't be empty"));
    }

    @Test
    public void testParsesNamesAndVersionsSimple() throws DetectableException, MissingExternalIdException, CycleDetectedException {
        List<CargoLockPackage> input = cargoLock(
            "[[package]]",
            "name = \"test1\"", "version = \"1.0.0\"",
            "",
            "[[package]]",
            "name = \"test2\"",
            "version = \"2.0.0\""
        );
        CargoLockTransformer cargoLockTransformer = new CargoLockTransformer();
        DependencyGraph graph = cargoLockTransformer.transformToGraph(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("test1", "1.0.0");
        graphAssert.hasRootDependency("test2", "2.0.0");
    }

    @Test
    public void multiplePackageDefinitionsTest() {
        List<CargoLockPackage> input = new ArrayList<>();
        input.add(new CargoLockPackage(new NameVersion("child1", "version1"), Collections.emptyList()));
        input.add(new CargoLockPackage(new NameVersion("child1", "version2"), Collections.emptyList()));
        input.add(new CargoLockPackage(new NameVersion("parent1", "anything"), Collections.singletonList(new NameOptionalVersion("child1"))));
        CargoLockTransformer cargoLockTransformer = new CargoLockTransformer();

        assertThrows(DetectableException.class, () -> cargoLockTransformer.transformToGraph(input));
    }

    @Test
    public void testParsesNoisyDependencyLines() throws DetectableException, MissingExternalIdException, CycleDetectedException {
        List<CargoLockPackage> input = cargoLock(
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
        DependencyGraph graph = cargoLockTransformer.transformToGraph(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph);
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("test1", "1.0.0");
        graphAssert.hasParentChildRelationship("test1", "1.0.0", "dep1", "0.5.0");
        graphAssert.hasParentChildRelationship("test1", "1.0.0", "dep2", "2.0.0");
    }

    @Test
    public void testCorrectNumberOfRootDependencies() throws DetectableException, MissingExternalIdException, CycleDetectedException {
        List<CargoLockPackage> input = cargoLock(
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
        DependencyGraph graph = cargoLockTransformer.transformToGraph(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph);
        graphAssert.hasRootSize(1);
    }
}
