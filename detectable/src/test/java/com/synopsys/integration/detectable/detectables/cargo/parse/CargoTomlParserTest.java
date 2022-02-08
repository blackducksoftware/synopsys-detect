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
        Optional<NameVersion> nameVersion = parseCargoTomlLines(
            "[package]",
            "name = \"my-name\"",
            "version = \"my-version\""
        );

        assertTrue(nameVersion.isPresent());
        assertEquals("my-name", nameVersion.get().getName());
        assertEquals("my-version", nameVersion.get().getVersion());
    }

    @Test
    void extractNameNoVersion() {
        Optional<NameVersion> nameVersion = parseCargoTomlLines(
            "[package]",
            "name = \"my-name\""
        );

        assertTrue(nameVersion.isPresent());
        assertEquals("my-name", nameVersion.get().getName());
        assertNull(nameVersion.get().getVersion());
    }

    @Test
    void extractNoName() {
        Optional<NameVersion> nameVersion = parseCargoTomlLines(
            "[package]",
            "some-other-key  = \"other-value\""
        );

        assertFalse(nameVersion.isPresent());
    }

    @Test
    void extractNoPackage() {
        Optional<NameVersion> nameVersion = parseCargoTomlLines(
            "[not-the-package-you-are-looking-for]",
            "some-other-key  = \"other-value\""
        );

        assertFalse(nameVersion.isPresent());
    }

    private Optional<NameVersion> parseCargoTomlLines(String... lines) {
        CargoTomlParser parser = new CargoTomlParser();
        String cargoTomlContents = StringUtils.joinWith(System.lineSeparator(), (Object[]) lines);
        return parser.parseNameVersionFromCargoToml(cargoTomlContents);
    }

}
