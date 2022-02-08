package com.synopsys.integration.configuration.property.types.enumsoft;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.configuration.parse.ValueParseException;

class SoftEnumValueParserTest {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "Thing ", " THING" })
    public void unknownValues(String expectedValue) throws ValueParseException {
        SoftEnumValue<Example> resolvedValue = new SoftEnumValueParser<>(Example.class).parse(expectedValue);
        if (resolvedValue.getSoftValue().isPresent()) {
            Assertions.assertEquals(expectedValue, resolvedValue.getSoftValue().get(), "Should parse value to a soft string.");
        } else {
            fail("Should have resolved to an soft string and not an enum.");
        }
    }

    @Test
    public void parsesEnumValue() throws ValueParseException {
        assertValidSoftEnum(Example.THING, "THING");
        assertValidSoftEnum(Example.ANOTHER, "ANOTHER");
        assertValidSoftEnum(Example.THIRD, "THIRD");
    }

    private void assertValidSoftEnum(Example expectedValue, String rawValue) throws ValueParseException {
        SoftEnumValue<Example> actualValue = new SoftEnumValueParser<>(Example.class).parse(rawValue);
        Assertions.assertEquals(SoftEnumValue.ofEnumValue(expectedValue), actualValue);
        Assertions.assertEquals(rawValue, actualValue.toString());
    }
}