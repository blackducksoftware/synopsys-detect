package com.blackduck.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.blackduck.integration.detectable.detectables.bitbake.data.BitbakeEnvironment;
import com.blackduck.integration.detectable.detectables.bitbake.parse.BitbakeEnvironmentParser;
import org.junit.jupiter.api.Test;

public class BitbakeEnvironmentParserTest {
    private static final String ARCH = "";
    private static final String LICENSES_DIR = "";
    private static final List<String> lines = Arrays.asList(
        "otherstuff",
        "MACHINE_ARCH=\"" + ARCH + "\"",
        "morestuff",
        "LICENSE_DIRECTORY=\"" + LICENSES_DIR + "\"",
        "yetmorestuff"
    );

    @Test
    void testParseEnvironment() {
        BitbakeEnvironmentParser parser = new BitbakeEnvironmentParser();

        BitbakeEnvironment environment = parser.parse(lines);

        assertTrue(environment.getMachineArch().isPresent());
        assertEquals(ARCH, environment.getMachineArch().get());

        assertTrue(environment.getLicensesDirPath().isPresent());
        assertEquals(LICENSES_DIR, environment.getLicensesDirPath().get());

        assertFalse(environment.getMachine().isPresent());
    }

    @Test
    void testParseEnvironmentMachinePresent() {
        BitbakeEnvironmentParser parser = new BitbakeEnvironmentParser();

        List<String> linesWithMachine = new ArrayList<>(lines);
        linesWithMachine.add("MACHINE=\"some_machine\"");

        BitbakeEnvironment environment = parser.parse(linesWithMachine);

        assertTrue(environment.getMachineArch().isPresent());
        assertEquals(ARCH, environment.getMachineArch().get());

        assertTrue(environment.getLicensesDirPath().isPresent());
        assertEquals(LICENSES_DIR, environment.getLicensesDirPath().get());

        assertTrue(environment.getMachine().isPresent());
        assertEquals("some_machine", environment.getMachine().get());
    }
}
