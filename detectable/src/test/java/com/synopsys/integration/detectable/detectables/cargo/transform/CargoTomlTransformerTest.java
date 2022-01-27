package com.synopsys.integration.detectable.detectables.cargo.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.cargo.data.CargoToml;
import com.synopsys.integration.detectable.detectables.cargo.data.CargoTomlPackage;
import com.synopsys.integration.util.NameVersion;

class CargoTomlTransformerTest {

    @Test
    void findProjectNameVersion() {
        CargoTomlTransformer transformer = new CargoTomlTransformer();
        CargoToml cargoToml = new CargoToml(new CargoTomlPackage("name", "version"));
        Optional<NameVersion> projectNameVersion = transformer.findProjectNameVersion(cargoToml);

        assertTrue(projectNameVersion.isPresent());
        assertEquals("name", projectNameVersion.get().getName());
        assertEquals("version", projectNameVersion.get().getVersion());
    }

    @Test
    void findProjectNameNoVersion() {
        CargoTomlTransformer transformer = new CargoTomlTransformer();
        CargoToml cargoToml = new CargoToml(new CargoTomlPackage("name", null));
        Optional<NameVersion> projectNameVersion = transformer.findProjectNameVersion(cargoToml);

        assertTrue(projectNameVersion.isPresent());
        assertEquals("name", projectNameVersion.get().getName());
        assertNull(null, projectNameVersion.get().getVersion());
    }

    @Test
    void findProjectNoNameOrVersion() {
        CargoTomlTransformer transformer = new CargoTomlTransformer();
        CargoToml cargoToml = new CargoToml(new CargoTomlPackage(null, null));
        Optional<NameVersion> projectNameVersion = transformer.findProjectNameVersion(cargoToml);

        assertFalse(projectNameVersion.isPresent());
    }

    @Test
    void findProjectPackage() {
        CargoTomlTransformer transformer = new CargoTomlTransformer();
        CargoToml cargoToml = new CargoToml(null);
        Optional<NameVersion> projectNameVersion = transformer.findProjectNameVersion(cargoToml);

        assertFalse(projectNameVersion.isPresent());
    }
}