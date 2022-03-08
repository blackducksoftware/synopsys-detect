package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.data.BitbakeEnvironment;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeEnvironmentParser;

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

        BitbakeEnvironment environment = parser.parseArchitecture(lines);

        assertTrue(environment.getMachineArch().isPresent());
        assertEquals(ARCH, environment.getMachineArch().get());

        assertTrue(environment.getLicensesDirPath().isPresent());
        assertEquals(LICENSES_DIR, environment.getLicensesDirPath().get());
    }
}
