package com.synopsys.integration.detectable.detectables.cargo.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.cargo.data.CargoLockPackageData;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLockPackage;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoDependencyLineParser;
import com.synopsys.integration.detectable.util.NameOptionalVersion;
import com.synopsys.integration.util.NameVersion;

class CargoLockDataDataTransformerTest {

    @Test
    void transform() {
        CargoDependencyLineParser cargoDependencyLineParser = new CargoDependencyLineParser();
        CargoLockDataTransformer transformer = new CargoLockDataTransformer(cargoDependencyLineParser);

        List<String> dependencies = Arrays.asList("dep1", "dep2 2.0.0", "dep3 3.0.0 (registry+https://some-registry-url");
        CargoLockPackageData cargoLockPackageData = new CargoLockPackageData("some-name", "some-version", null, null, dependencies);
        CargoLockPackage cargoLockPackage = transformer.transform(cargoLockPackageData);

        assertEquals(new NameVersion("some-name", "some-version"), cargoLockPackage.getPackageNameVersion());
        assertEquals(3, cargoLockPackage.getDependencies().size());

        NameOptionalVersion dep1Actual = cargoLockPackage.getDependencies().get(0);
        assertEquals("dep1", dep1Actual.getName());
        assertFalse(dep1Actual.getVersion().isPresent());

        NameOptionalVersion dep2Actual = cargoLockPackage.getDependencies().get(1);
        assertEquals("dep2", dep2Actual.getName());
        assertTrue(dep2Actual.getVersion().isPresent());
        assertEquals("2.0.0", dep2Actual.getVersion().get());

        NameOptionalVersion dep3Actual = cargoLockPackage.getDependencies().get(2);
        assertEquals("dep3", dep3Actual.getName());
        assertTrue(dep3Actual.getVersion().isPresent());
        assertEquals("3.0.0", dep3Actual.getVersion().get());
    }

    @Test
    void transformNoVersion() {
        CargoDependencyLineParser cargoDependencyLineParser = new CargoDependencyLineParser();
        CargoLockDataTransformer transformer = new CargoLockDataTransformer(cargoDependencyLineParser);

        CargoLockPackageData cargoLockPackageData = new CargoLockPackageData("some-name", null, null, null, Collections.emptyList());
        CargoLockPackage cargoLockPackage = transformer.transform(cargoLockPackageData);

        assertEquals(new NameVersion("some-name", null), cargoLockPackage.getPackageNameVersion());
        assertTrue(cargoLockPackage.getDependencies().isEmpty());
    }
}