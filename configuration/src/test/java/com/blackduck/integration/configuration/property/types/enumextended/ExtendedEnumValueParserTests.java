package com.blackduck.integration.configuration.property.types.enumextended;

import com.blackduck.integration.configuration.parse.ValueParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ExtendedEnumValueParserTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    private enum ExampleExtended {
        FOURTH
    }

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "Thing ", " THING" })
    public void unknownValues(String value) {
        Assertions.assertThrows(ValueParseException.class, () -> new ExtendedEnumValueParser<>(ExampleExtended.class, Example.class).parse(value));
    }

    @Test
    public void parsesLowercaseEnumValue() throws ValueParseException {
        Assertions.assertEquals(
            ExtendedEnumValue.ofBaseValue(Example.THING).getBaseValue(),
            new ExtendedEnumValueParser<>(ExampleExtended.class, Example.class).parse("tHiNg").getBaseValue()
        );
        Assertions.assertEquals(
            ExtendedEnumValue.ofBaseValue(Example.ANOTHER).getBaseValue(),
            new ExtendedEnumValueParser<>(ExampleExtended.class, Example.class).parse("another").getBaseValue()
        );
    }

    @Test
    public void parsesEnumValue() throws ValueParseException {
        Assertions.assertEquals(
            ExtendedEnumValue.ofBaseValue(Example.THING).getBaseValue(),
            new ExtendedEnumValueParser<>(ExampleExtended.class, Example.class).parse("THING").getBaseValue()
        );
        Assertions.assertEquals(
            ExtendedEnumValue.ofBaseValue(Example.ANOTHER).getBaseValue(),
            new ExtendedEnumValueParser<>(ExampleExtended.class, Example.class).parse("ANOTHER").getBaseValue()
        );
        Assertions.assertEquals(
            ExtendedEnumValue.ofBaseValue(Example.THIRD).getBaseValue(),
            new ExtendedEnumValueParser<>(ExampleExtended.class, Example.class).parse("THIRD").getBaseValue()
        );
        Assertions.assertEquals(
            ExtendedEnumValue.ofExtendedValue(ExampleExtended.FOURTH).getBaseValue(),
            new ExtendedEnumValueParser<>(ExampleExtended.class, Example.class).parse("FOURTH").getBaseValue()
        );
    }
}