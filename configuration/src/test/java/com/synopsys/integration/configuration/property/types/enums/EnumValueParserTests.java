package com.synopsys.integration.configuration.property.types.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.configuration.parse.ValueParseException;

public class EnumValueParserTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    private final EnumValueParser<Example> parser = new EnumValueParser<>(Example.class);

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "Thing ", " THING" })
    public void unknownValues(String value) {
        Assertions.assertThrows(ValueParseException.class, () -> parser.parse(value));
    }

    @Test
    public void parsesEnumValue() throws ValueParseException {
        Assertions.assertEquals(Example.THING, parser.parse("THING"));
        Assertions.assertEquals(Example.ANOTHER, parser.parse("ANOTHER"));
        Assertions.assertEquals(Example.THIRD, parser.parse("THIRD"));
    }

    @Test
    public void parsesLowercaseEnumValue() throws ValueParseException {
        Assertions.assertEquals(Example.THING, parser.parse("tHiNg"));
        Assertions.assertEquals(Example.THING, parser.parse("thing"));
    }

}