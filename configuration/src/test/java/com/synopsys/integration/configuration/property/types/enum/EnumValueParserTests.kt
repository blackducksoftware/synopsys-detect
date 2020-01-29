package com.synopsys.integration.configuration.property.types.enum

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.property.types.enums.EnumValueParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class EnumValueParserTests {
    enum class Example {
        THING,
        ANOTHER,
        THIRD
    }

    @ParameterizedTest
    @ValueSource(strings = ["unknown", "Thing ", " THING", "tHiNg"])
    fun unknownValues(value: String) {
        Assertions.assertThrows(ValueParseException::class.java) {
            EnumValueParser(Example::class.java).parse(value)
        }
    }

    @Test
    fun parsesEnumValue() {
        Assertions.assertEquals(Example.THING, EnumValueParser(Example::class.java).parse("THING"))
        Assertions.assertEquals(Example.ANOTHER, EnumValueParser(Example::class.java).parse("ANOTHER"))
        Assertions.assertEquals(Example.THIRD, EnumValueParser(Example::class.java).parse("THIRD"))
    }
}