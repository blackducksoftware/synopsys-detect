package com.synopsys.integration.configuration.property.types.longs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.configuration.parse.ValueParseException;

public class LongValueParserTests {
    private final LongValueParser parser = new LongValueParser();

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "Nan", "", " 1", "1L", "9223372036854775808" })
    public void parseUnknownThrows(String value) {
        Assertions.assertThrows(ValueParseException.class, () -> parser.parse(value));
    }

    @Test
    public void parseLong() throws ValueParseException {
        Assertions.assertEquals(new Long(-1), parser.parse("-1"));
        Assertions.assertEquals(new Long(1), parser.parse("1"));
        Assertions.assertEquals(new Long(Long.MAX_VALUE), parser.parse("9223372036854775807"));
        Assertions.assertEquals(new Long(Long.MIN_VALUE), parser.parse("-9223372036854775808"));
    }
}