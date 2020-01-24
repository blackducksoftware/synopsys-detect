package com.synopsys.integration.configuration.config.path

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.property.types.path.PathValue
import com.synopsys.integration.configuration.property.types.path.PathValueParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PathValueParserTests() {
    @ParameterizedTest()
    @ValueSource(strings = ["", " ", "     "])
    fun parseEmpty(value: String) {
        Assertions.assertThrows(ValueParseException::class.java) {
            PathValueParser().parse(value)
        }
    }

    @Test
    fun parseValid() {
        Assertions.assertEquals(PathValue("/valid/path"), PathValueParser().parse("/valid/path"))
    }
}