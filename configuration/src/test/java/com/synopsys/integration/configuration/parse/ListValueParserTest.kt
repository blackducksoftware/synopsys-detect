/**
 * configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
    class TestCustomListValueParser(valueParser: ValueParser<String>, delimiter: String) : ListValueParser<String>(valueParser, delimiter)

    @Test
    fun parseDefault() {
        val listValueParser = TestDefaultListValueParser(TestValueParser())
        val actualValues = listValueParser.parse("test,this,example , parser")
        Assertions.assertEquals(listOf("test", "this", "example", "parser"), actualValues, "The list parser should be splitting on comma and trimming by default.")
    }

    @Test
    fun parseCustomDelimiters() {
        val listValueParser = TestCustomListValueParser(TestValueParser(), "|")
        val actualValues = listValueParser.parse("test this|parser|for real")
        Assertions.assertEquals(listOf("test this", "parser", "for real"), actualValues)
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