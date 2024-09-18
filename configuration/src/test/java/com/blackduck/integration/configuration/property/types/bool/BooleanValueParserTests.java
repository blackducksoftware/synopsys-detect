package com.blackduck.integration.configuration.property.types.bool;

import com.blackduck.integration.configuration.parse.ValueParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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