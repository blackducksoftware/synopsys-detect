package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeEnvironment;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeEnvironmentParser;

public class BitbakeEnvironmentParserTest {

    @Test
    void testParse() {
        List<String> lines = Arrays.asList(
            "otherstuff",
            "MACHINE_ARCH=\"testarch\"",
            "morestuff",
            "LICENSE_DIRECTORY=\"/workdir/poky/build/tmp/deploy/licenses\"",
            "yetmorestuff");

        BitbakeEnvironmentParser parser = new BitbakeEnvironmentParser();

        BitbakeEnvironment environment = parser.parseArchitecture(lines);

        assertTrue(environment.getMachineArch().isPresent());
        assertEquals("testarch", environment.getMachineArch().get());
    }
}
