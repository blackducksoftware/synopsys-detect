package com.synopsys.integration.detectable.detectables.cargo.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.cargo.data.CargoTomlData;
import com.synopsys.integration.detectable.detectables.cargo.data.CargoTomlPackageData;
import com.synopsys.integration.util.NameVersion;

class CargoTomlDataTransformerTest {

    @Test
    void findProjectNameVersion() {
        CargoTomlDataTransformer transformer = new CargoTomlDataTransformer();
        CargoTomlData cargoTomlData = new CargoTomlData(new CargoTomlPackageData("name", "version"));
        Optional<NameVersion> projectNameVersion = transformer.findProjectNameVersion(cargoTomlData);

        assertTrue(projectNameVersion.isPresent());
        assertEquals("name", projectNameVersion.get().getName());
        assertEquals("version", projectNameVersion.get().getVersion());
    }

    @Test
    void findProjectNameNoVersion() {
        CargoTomlDataTransformer transformer = new CargoTomlDataTransformer();
        CargoTomlData cargoTomlData = new CargoTomlData(new CargoTomlPackageData("name", null));
        Optional<NameVersion> projectNameVersion = transformer.findProjectNameVersion(cargoTomlData);

        assertTrue(projectNameVersion.isPresent());
        assertEquals("name", projectNameVersion.get().getName());
        assertNull(null, projectNameVersion.get().getVersion());
    }

    @Test
    void findProjectNoNameOrVersion() {
        CargoTomlDataTransformer transformer = new CargoTomlDataTransformer();
        CargoTomlData cargoTomlData = new CargoTomlData(new CargoTomlPackageData(null, null));
        Optional<NameVersion> projectNameVersion = transformer.findProjectNameVersion(cargoTomlData);

        assertFalse(projectNameVersion.isPresent());
    }

    @Test
    void findProjectPackage() {
        CargoTomlDataTransformer transformer = new CargoTomlDataTransformer();
        CargoTomlData cargoTomlData = new CargoTomlData(null);
        Optional<NameVersion> projectNameVersion = transformer.findProjectNameVersion(cargoTomlData);

        assertFalse(projectNameVersion.isPresent());
    }
}