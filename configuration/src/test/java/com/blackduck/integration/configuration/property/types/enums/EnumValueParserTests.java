package com.blackduck.integration.configuration.property.types.enums;

import com.blackduck.integration.configuration.parse.ValueParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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