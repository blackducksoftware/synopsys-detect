package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeEnvironmentParser;

public class BitbakeEnvironmentParserTest {

    @Test
    void testParse() {
        List<String> lines = Arrays.asList(
            "otherstuff",
            "MACHINE_ARCH=\"testarch\"",
            "morestuff");

        BitbakeEnvironmentParser parser = new BitbakeEnvironmentParser();

        Optional<String> arch = parser.parseArchitecture(lines);

        assertTrue(arch.isPresent());
        assertEquals("testarch", arch.get());
    }
}
