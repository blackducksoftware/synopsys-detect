package com.synopsys.integration.detectable.detectables.cargo.transform;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLockPackage;
import com.synopsys.integration.detectable.util.CycleDetectedException;
import com.synopsys.integration.detectable.util.NameOptionalVersion;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;
import com.synopsys.integration.util.NameVersion;

public class CargoLockDataTransformerTest {

    @Test
    public void testParsesNamesAndVersionsSimple() throws DetectableException, MissingExternalIdException, CycleDetectedException {
        List<CargoLockPackage> input = new ArrayList<>();
        input.add(createPackage("test1", "1.0.0"));
        input.add(createPackage("test2", "2.0.0"));
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
        input.add(createPackage("child1", "version1"));
        input.add(createPackage("child1", "version2"));
        input.add(createPackage("parent1", "anything", new NameOptionalVersion("child1")));
        CargoLockTransformer cargoLockTransformer = new CargoLockTransformer();

        assertThrows(DetectableException.class, () -> cargoLockTransformer.transformToGraph(input));
    }

    @Test
    public void testCorrectNumberOfRootDependencies() throws DetectableException, MissingExternalIdException, CycleDetectedException {
        List<CargoLockPackage> input = new ArrayList<>();
        input.add(createPackage("test1", "1.0.0",
            new NameOptionalVersion("dep1"),
            new NameOptionalVersion("dep2")
        ));
        input.add(createPackage("dep1", "0.5.0"));
        input.add(createPackage("dep2", "0.6.0"));
        CargoLockTransformer cargoLockTransformer = new CargoLockTransformer();
        DependencyGraph graph = cargoLockTransformer.transformToGraph(input);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph);
        graphAssert.hasRootSize(1);
    }

    private CargoLockPackage createPackage(String name, String version, NameOptionalVersion... dependencies) {
        return new CargoLockPackage(new NameVersion(name, version), Arrays.asList(dependencies));
    }
}
