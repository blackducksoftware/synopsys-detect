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
package com.synopsys.integration.configuration.parse;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;

class ListValueParserTest {
    private static class TestValueParser extends ValueParser<String> {
        @NotNull
        @Override
        public String parse(@NotNull final String value) throws ValueParseException {
            if ("-1".equals(value)) {
                throw new ValueParseException(value, "String", "Can convert anything but this value to a String.");
            }
            return value;
        }
    }

    private static class TestDefaultListValueParser extends ListValueParser<String> {
        final ValueParser<String> valueParser;

        public TestDefaultListValueParser(final ValueParser<String> valueParser) {
            super(valueParser);
            this.valueParser = valueParser;
        }
    }

    private static class TestCustomListValueParser extends ListValueParser<String> {
        final ValueParser<String> valueParser;

        public TestCustomListValueParser(final ValueParser<String> valueParser, final String delimiter) {
            super(valueParser, delimiter);
            this.valueParser = valueParser;
        }
    }

    @Test
    public void parseDefault() throws ValueParseException {
        final ListValueParser<String> listValueParser = new TestDefaultListValueParser(new TestValueParser());
        final List<String> actualValues = listValueParser.parse("test,this,example , parser");
        Assertions.assertEquals(Bds.listOf("test", "this", "example", "parser"), actualValues, "The list parser should be splitting on comma and trimming by default.");
    }

    @Test
    public void parseCustomDelimiters() throws ValueParseException {
        final ListValueParser<String> listValueParser = new TestCustomListValueParser(new TestValueParser(), "|");
        final List<String> actualValues = listValueParser.parse("test this|parser|for real");
        Assertions.assertEquals(Bds.listOf("test this", "parser", "for real"), actualValues);
    }

    @Test
    public void failsToParseInvalidElement() {
        final ListValueParser<String> listValueParser = new TestDefaultListValueParser(new TestValueParser());
        Assertions.assertThrows(ValueParseException.class, () -> listValueParser.parse("test,should,throw,-1,for,test"));
    }

    @Test
    public void failsToParseEmpty() {
        final ListValueParser<String> listValueParser = new TestDefaultListValueParser(new TestValueParser());
        Assertions.assertThrows(ValueParseException.class, () -> listValueParser.parse("should,,throw"));
    }

    @Test
    public void failsToParseWhitespace() {
        final ListValueParser<String> listValueParser = new TestDefaultListValueParser(new TestValueParser());
        Assertions.assertThrows(ValueParseException.class, () -> listValueParser.parse("should,  ,throw"));
    }
}