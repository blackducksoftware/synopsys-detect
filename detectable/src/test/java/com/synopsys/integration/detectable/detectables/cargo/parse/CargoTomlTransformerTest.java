package com.synopsys.integration.detectable.detectables.cargo.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.cargo.data.CargoToml;
import com.synopsys.integration.detectable.detectables.cargo.data.CargoTomlPackage;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoTomlTransformer;
import com.synopsys.integration.util.NameVersion;

class CargoTomlTransformerTest {
    @Test
    void extractNameVersion() {
        CargoTomlTransformer cargoTomlTransformer = new CargoTomlTransformer();
        CargoToml cargoToml = new CargoToml(new CargoTomlPackage("my-name", "my-version"));
        Optional<NameVersion> nameVersion = cargoTomlTransformer.findProjectNameVersion(cargoToml);

        assertTrue(nameVersion.isPresent());
        assertEquals("my-name", nameVersion.get().getName());
        assertEquals("my-version", nameVersion.get().getVersion());
    }

    @Test
    void extractNameNoVersion() {
        CargoTomlTransformer cargoTomlTransformer = new CargoTomlTransformer();
        CargoToml cargoToml = new CargoToml(new CargoTomlPackage("my-name", null));
        Optional<NameVersion> nameVersion = cargoTomlTransformer.findProjectNameVersion(cargoToml);

        assertTrue(nameVersion.isPresent());
        assertEquals("my-name", nameVersion.get().getName());
        assertNull(nameVersion.get().getVersion());
    }

    @Test
    void extractNoName() {
        CargoTomlTransformer cargoTomlTransformer = new CargoTomlTransformer();
        CargoToml cargoToml = new CargoToml(new CargoTomlPackage(null, null));

        Optional<NameVersion> nameVersion = cargoTomlTransformer.findProjectNameVersion(cargoToml);
        assertFalse(nameVersion.isPresent());
    }

    @Test
    void extractNoPackage() {
        CargoTomlTransformer cargoTomlTransformer = new CargoTomlTransformer();
        CargoToml cargoToml = new CargoToml(null);

        Optional<NameVersion> nameVersion = cargoTomlTransformer.findProjectNameVersion(cargoToml);
        assertFalse(nameVersion.isPresent());
    }

}
