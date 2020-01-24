package com.synopsys.integration.configuration.config.integer

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.property.types.integer.IntegerValueParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class IntegerValueParserTests() {
    @ParameterizedTest()
    @ValueSource(strings = ["unknown", "Nan", "", " 1", "9223372036854775807"])
    fun parseUnknownThrows(value: String) {
        Assertions.assertThrows(ValueParseException::class.java) {
            IntegerValueParser().parse(value)
        }
    }

    @Test
    fun parseInt() {
        fun assert(expected: Int, value: String) {
            Assertions.assertEquals(expected, IntegerValueParser().parse(value))
        }

        assert(-1, "-1")
        assert(1, "1")
        assert(Integer.MAX_VALUE, "2147483647")
        assert(Integer.MIN_VALUE, "-2147483648")
    }
}