package com.synopsys.integration.configuration.property.types.enumfilterable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.configuration.parse.ValueParseException;

class FilterableEnumValueParserTests {
    private final FilterableEnumValueParser<Example> parser = new FilterableEnumValueParser<>(Example.class);

    private enum Example {
        THING,
        ANOTHER
    }

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "Thing ", " THING", "fourth" })
    public void unknownValues(String value) {
        Assertions.assertThrows(ValueParseException.class, () -> parser.parse(value));
    }

    @Test
    public void parsesLowercaseEnumValue() throws ValueParseException {
        Assertions.assertEquals(FilterableEnumValue.value(Example.THING), parser.parse("tHiNg"));
        Assertions.assertEquals(FilterableEnumValue.value(Example.ANOTHER), parser.parse("another"));
    }

    @Test
    public void parsesEnumValue() throws ValueParseException {
        Assertions.assertEquals(FilterableEnumValue.value(Example.THING), parser.parse("THING"));
        Assertions.assertEquals(FilterableEnumValue.value(Example.ANOTHER), parser.parse("ANOTHER"));
        Assertions.assertEquals(FilterableEnumValue.allValue().toString(), parser.parse("ALL").toString());
        Assertions.assertEquals(FilterableEnumValue.noneValue().toString(), parser.parse("NONE").toString());
    }
}