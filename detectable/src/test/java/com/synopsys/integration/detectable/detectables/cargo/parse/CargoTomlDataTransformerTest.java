package com.synopsys.integration.detectable.detectables.cargo.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.cargo.data.CargoTomlData;
import com.synopsys.integration.detectable.detectables.cargo.data.CargoTomlPackageData;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoTomlDataTransformer;
import com.synopsys.integration.util.NameVersion;

class CargoTomlDataTransformerTest {
    @Test
    void extractNameVersion() {
        CargoTomlDataTransformer cargoTomlDataTransformer = new CargoTomlDataTransformer();
        CargoTomlData cargoTomlData = new CargoTomlData(new CargoTomlPackageData("my-name", "my-version"));
        Optional<NameVersion> nameVersion = cargoTomlDataTransformer.findProjectNameVersion(cargoTomlData);

        assertTrue(nameVersion.isPresent());
        assertEquals("my-name", nameVersion.get().getName());
        assertEquals("my-version", nameVersion.get().getVersion());
    }

    @Test
    void extractNameNoVersion() {
        CargoTomlDataTransformer cargoTomlDataTransformer = new CargoTomlDataTransformer();
        CargoTomlData cargoTomlData = new CargoTomlData(new CargoTomlPackageData("my-name", null));
        Optional<NameVersion> nameVersion = cargoTomlDataTransformer.findProjectNameVersion(cargoTomlData);

        assertTrue(nameVersion.isPresent());
        assertEquals("my-name", nameVersion.get().getName());
        assertNull(nameVersion.get().getVersion());
    }

    @Test
    void extractNoName() {
        CargoTomlDataTransformer cargoTomlDataTransformer = new CargoTomlDataTransformer();
        CargoTomlData cargoTomlData = new CargoTomlData(new CargoTomlPackageData(null, null));

        Optional<NameVersion> nameVersion = cargoTomlDataTransformer.findProjectNameVersion(cargoTomlData);
        assertFalse(nameVersion.isPresent());
    }

    @Test
    void extractNoPackage() {
        CargoTomlDataTransformer cargoTomlDataTransformer = new CargoTomlDataTransformer();
        CargoTomlData cargoTomlData = new CargoTomlData(null);

        Optional<NameVersion> nameVersion = cargoTomlDataTransformer.findProjectNameVersion(cargoTomlData);
        assertFalse(nameVersion.isPresent());
    }

}
