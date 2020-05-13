package com.synopsys.integration.detectable.detectables.cargo.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoLockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class CargoLockParserTest {

    @Test
    public void testParsesNamesAndVersionsSimple() throws DetectableException {

        CargoLockParser cargoLockParser = new CargoLockParser();
        DependencyGraph graph = cargoLockParser.parseLockFile(FunctionalTestFiles.asInputStream("/cargo/simpleNameAndVersion.lock"));

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("test1", "1.0.0");
        graphAssert.hasRootDependency("test2", "2.0.0");
    }

    @Test
    public void testParsesNoisyDependencyLines() throws DetectableException {

        CargoLockParser cargoLockParser = new CargoLockParser();
        DependencyGraph graph = cargoLockParser.parseLockFile(FunctionalTestFiles.asInputStream("/cargo/noisyDependencyLines.lock"));

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph);
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("test1", "1.0.0");
        graphAssert.hasParentChildRelationship("test1", "1.0.0", "dep1", "0.5.0");
        graphAssert.hasParentChildRelationship("test1", "1.0.0", "dep2", "2.0.0");
    }

    @Test
    public void testCorrectNumberOfRootDependencies() throws DetectableException {
        CargoLockParser cargoLockParser = new CargoLockParser();
        DependencyGraph graph = cargoLockParser.parseLockFile(FunctionalTestFiles.asInputStream("/cargo/dependencyHierarchy.lock"));

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.CRATES, graph);
        graphAssert.hasRootSize(1);
    }

    @Test
    public void testCatchInvalidSyntaxInLockFile() {
        boolean caughtException = false;
        try {
            CargoLockParser cargoLockParser = new CargoLockParser();
            DependencyGraph graph = cargoLockParser.parseLockFile(FunctionalTestFiles.asInputStream("/cargo/invalidSyntax.lock"));
        } catch (DetectableException e) {
            caughtException = true;
        }
        Assertions.assertTrue(caughtException);
    }
}
