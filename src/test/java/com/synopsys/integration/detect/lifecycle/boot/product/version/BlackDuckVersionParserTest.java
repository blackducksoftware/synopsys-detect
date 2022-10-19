package com.synopsys.integration.detect.lifecycle.boot.product.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class BlackDuckVersionParserTest {

    @Test
    void test() {
        BlackDuckVersionParser blackDuckVersionParser = new BlackDuckVersionParser();

        assertEquals(2021, blackDuckVersionParser.parse("2021.10.0").get().getMajor());
        assertEquals(10, blackDuckVersionParser.parse("2021.10.0").get().getMinor());
        assertEquals(0, blackDuckVersionParser.parse("2021.10.0").get().getPatch());

        assertEquals(2021, blackDuckVersionParser.parse("2021.10.0-SNAPSHOT").get().getMajor());
        assertEquals(10, blackDuckVersionParser.parse("2021.10.0-SNAPSHOT").get().getMinor());
        assertEquals(0, blackDuckVersionParser.parse("2021.10.0-SNAPSHOT").get().getPatch());
        
        assertFalse(blackDuckVersionParser.parse("totalgarbage").isPresent());
    }
}
