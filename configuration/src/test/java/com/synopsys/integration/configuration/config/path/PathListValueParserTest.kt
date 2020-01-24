package com.synopsys.integration.configuration.config.path

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.property.types.path.PathValue
import com.synopsys.integration.configuration.property.types.pathlist.PathListValueParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PathListValueParserTest {
    @ParameterizedTest
    @ValueSource(strings = ["", " ", "     "])
    fun parseEmpty(invalidValue: String) {
        val value = "/valid/path,$invalidValue,/another/valid/path"
        println("Testing value: \"$value\"")
        Assertions.assertThrows(ValueParseException::class.java) {
            PathListValueParser().parse(value)
        }
    }

    @Test
    fun parseValid() {
        val expectedPathValues = listOf(
                PathValue("/valid/path"),
                PathValue("/valid/path2"),
                PathValue("another/valid/path")
        )
        val actualPathValues = PathListValueParser().parse("/valid/path,/valid/path2,another/valid/path")
        Assertions.assertEquals(expectedPathValues, actualPathValues)
    }
}