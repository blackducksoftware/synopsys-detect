package com.synopsys.integration.detectable.detectables.bitbake.unit;

import java.util.Optional;

import org.junit.Test;

import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeArchitectureParser;

public class BitbakeArchitectureParserTest {
    @Test
    public void parseTargetArchitectureTest() {
        String line = "TARGET_SYS           = \"i586-poky-linux\"";

        final BitbakeArchitectureParser bitbakeArchitectureParser = new BitbakeArchitectureParser();
        final Optional<String> architecture = bitbakeArchitectureParser.architectureFromLine(line);

        assert architecture.get().equals("i586-poky-linux");
    }
}
