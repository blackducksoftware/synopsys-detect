package com.synopsys.integration.configuration.property.types.bool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.configuration.parse.ValueParseException;

class BooleanValueParserTests {
    @ParameterizedTest
    @ValueSource(strings = { "unknown", "we ird tef ", "243354323", "@Q@ASD" })
    public void parseUnknownThrows(String value) {
        Assertions.assertThrows(ValueParseException.class, () -> new BooleanValueParser().parse(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "tRuE", "true ", " true", "    ", "", "t" })
    public void booleanParsesTrue(String value) throws ValueParseException {
        Assertions.assertTrue(new BooleanValueParser().parse(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "false", "f" })
    public void booleanParsesFalse(String value) throws ValueParseException {
        Assertions.assertFalse(new BooleanValueParser().parse(value));
    }
}