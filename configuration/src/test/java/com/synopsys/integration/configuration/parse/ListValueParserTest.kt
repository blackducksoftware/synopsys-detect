package com.synopsys.integration.configuration.parse

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ListValueParserTest {

    class TestValueParser : ValueParser<String>() {
        override fun parse(value: String): String {
            if (value == "-1") {
                throw ValueParseException(value, "String", "Can convert anything but this value to a String.")
            }
            return value
        }
    }

    class TestDefaultListValueParser(valueParser: ValueParser<String>) : ListValueParser<String>(valueParser)
    class TestCustomListValueParser(valueParser: ValueParser<String>, vararg delimiters: String) : ListValueParser<String>(valueParser, *delimiters)

    @Test
    fun parseDefault() {
        val listValueParser = TestDefaultListValueParser(TestValueParser())
        val actualValues = listValueParser.parse("test,this,example , parser")
        Assertions.assertEquals(listOf("test", "this", "example", "parser"), actualValues, "The list parser should be splitting on comma and trimming by default.")
    }

    @Test
    fun parseCustomDelimiters() {
        val listValueParser = TestCustomListValueParser(TestValueParser(), " ", "|")
        val actualValues = listValueParser.parse("test this|parser|for real")
        Assertions.assertEquals(listOf("test", "this", "parser", "for", "real"), actualValues)
    }

    @Test
    fun failsToParseInvalidElement() {
        val listValueParser = TestDefaultListValueParser(TestValueParser())
        Assertions.assertThrows(ValueParseException::class.java) {
            listValueParser.parse("test,should,throw,-1,for,test")
        }
    }

    @Test
    fun failsToParseEmpty() {
        val listValueParser = TestDefaultListValueParser(TestValueParser())
        Assertions.assertThrows(ValueParseException::class.java) {
            listValueParser.parse("should,,throw")
        }
    }

    @Test
    fun failsToParseWhitespace() {
        val listValueParser = TestDefaultListValueParser(TestValueParser())
        Assertions.assertThrows(ValueParseException::class.java) {
            listValueParser.parse("should,  ,throw")
        }
    }
}