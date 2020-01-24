package com.synopsys.integration.configuration.config.long

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.property.types.longs.LongValueParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LongValueParserTests {
    @ParameterizedTest
    @ValueSource(strings = ["unknown", "Nan", "", " 1", "1L", "9223372036854775808"])
    fun parseUnknownThrows(value: String) {
        Assertions.assertThrows(ValueParseException::class.java) {
            LongValueParser().parse(value)
        }
    }

    @Test
    fun parseLong() {
        fun assert(expected: Long, value: String) {
            Assertions.assertEquals(expected, LongValueParser().parse(value))
        }

        assert(-1, "-1")
        assert(1, "1")
        assert(Long.MAX_VALUE, "9223372036854775807")
        assert(Long.MIN_VALUE, "-9223372036854775808")
    }
}