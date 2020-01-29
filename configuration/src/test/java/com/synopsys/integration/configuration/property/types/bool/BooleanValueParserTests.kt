package com.synopsys.integration.configuration.property.types.bool

import com.synopsys.integration.configuration.parse.ValueParseException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class BooleanValueParserTests {
    @ParameterizedTest
    @ValueSource(strings = ["unknown", "we ird tef ", "243354323", "@Q@ASD"])
    fun parseUnknownThrows(value: String) {
        Assertions.assertThrows(ValueParseException::class.java) {
            BooleanValueParser().parse(value)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["tRuE", "true ", " true", "    ", "", "t"])
    fun booleanParsesTrue(value: String) {
        Assertions.assertTrue(BooleanValueParser().parse(value))
    }

    @ParameterizedTest
    @ValueSource(strings = ["false", "f"])
    fun booleanParsesFalse(value: String) {
        Assertions.assertFalse(BooleanValueParser().parse(value))
    }
}