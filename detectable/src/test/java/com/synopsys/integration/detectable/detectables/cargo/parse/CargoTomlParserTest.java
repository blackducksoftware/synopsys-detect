package com.synopsys.integration.detectable.detectables.cargo.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.util.NameVersion;

class CargoTomlParserTest {
    @Test
    void extractNameVersion() {
        String cargoTomlContents = StringUtils.joinWith(System.lineSeparator(),
            "[package]",
            "name = \"my-name\"",
            "version = \"my-version\""
        );
        CargoTomlParser parser = new CargoTomlParser();
        Optional<NameVersion> nameVersion = parser.parseNameVersionFromCargoToml(cargoTomlContents);

        assertTrue(nameVersion.isPresent());
        assertEquals("my-name", nameVersion.get().getName());
        assertEquals("my-version", nameVersion.get().getVersion());
    }

    @Test
    void extractNameNoVersion() {
        String cargoTomlContents = StringUtils.joinWith(System.lineSeparator(),
            "[package]",
            "name = \"my-name\""
        );
        CargoTomlParser parser = new CargoTomlParser();
        Optional<NameVersion> nameVersion = parser.parseNameVersionFromCargoToml(cargoTomlContents);

        assertTrue(nameVersion.isPresent());
        assertEquals("my-name", nameVersion.get().getName());
        assertNull(nameVersion.get().getVersion());
    }

    @Test
    void extractNoName() {
        String cargoTomlContents = StringUtils.joinWith(System.lineSeparator(),
            "[package]",
            "some-other-key  = \"other-value\""
        );
        CargoTomlParser parser = new CargoTomlParser();
        Optional<NameVersion> nameVersion = parser.parseNameVersionFromCargoToml(cargoTomlContents);

        assertFalse(nameVersion.isPresent());
    }

    @Test
    void extractNoPackage() {

        String cargoTomlContents = StringUtils.joinWith(System.lineSeparator(),
            "[not-the-package-you-are-looking-for]",
            "some-other-key  = \"other-value\""
        );
        CargoTomlParser parser = new CargoTomlParser();
        Optional<NameVersion> nameVersion = parser.parseNameVersionFromCargoToml(cargoTomlContents);

        assertFalse(nameVersion.isPresent());
    }

}
