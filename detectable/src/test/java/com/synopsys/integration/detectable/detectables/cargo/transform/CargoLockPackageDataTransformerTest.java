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

class CargoLockPackageDataTransformerTest {

    @Test
    void transform() {
        CargoDependencyLineParser cargoDependencyLineParser = new CargoDependencyLineParser();
        CargoLockPackageDataTransformer transformer = new CargoLockPackageDataTransformer(cargoDependencyLineParser);

        List<String> dependencies = Arrays.asList("dep1", "dep2 2.0.0", "dep3 3.0.0 (registry+https://some-registry-url");
        CargoLockPackageData cargoLockPackageData = new CargoLockPackageData("some-name", "some-version", null, null, dependencies);
        CargoLockPackage cargoLockPackage = transformer.transform(cargoLockPackageData);

        assertEquals(new NameVersion("some-name", "some-version"), cargoLockPackage.getPackageNameVersion());
        assertEquals(3, cargoLockPackage.getDependencies().size());

        NameOptionalVersion dep1Actual = cargoLockPackage.getDependencies().get(0);
        assertEquals("dep1", dep1Actual.getName());
        assertFalse(dep1Actual.getVersion().isPresent());

        assertPackageNameVersion("dep2", "2.0.0", cargoLockPackage.getDependencies().get(1));
        assertPackageNameVersion("dep3", "3.0.0", cargoLockPackage.getDependencies().get(2));
    }

    private void assertPackageNameVersion(String expectedName, String expectedVersion, NameOptionalVersion actual) {
        assertEquals(expectedName, actual.getName());
        assertTrue(actual.getVersion().isPresent());
        assertEquals(expectedVersion, actual.getVersion().get());
    }

    @Test
    void transformNoVersion() {
        CargoDependencyLineParser cargoDependencyLineParser = new CargoDependencyLineParser();
        CargoLockPackageDataTransformer transformer = new CargoLockPackageDataTransformer(cargoDependencyLineParser);

        CargoLockPackageData cargoLockPackageData = new CargoLockPackageData("some-name", null, null, null, Collections.emptyList());
        CargoLockPackage cargoLockPackage = transformer.transform(cargoLockPackageData);

        assertEquals(new NameVersion("some-name", null), cargoLockPackage.getPackageNameVersion());
        assertTrue(cargoLockPackage.getDependencies().isEmpty());
    }
}