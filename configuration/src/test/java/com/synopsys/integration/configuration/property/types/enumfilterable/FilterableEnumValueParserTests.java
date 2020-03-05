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
package com.synopsys.integration.configuration.property.types.enumfilterable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.configuration.parse.ValueParseException;

class FilterableEnumValueParserTests {
    private final FilterableEnumValueParser<Example> parser = new FilterableEnumValueParser<>(Example.class);

    private enum Example {
        THING,
        ANOTHER
    }

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "Thing ", " THING", "fourth" })
    public void unknownValues(final String value) {
        Assertions.assertThrows(ValueParseException.class, () -> parser.parse(value));
    }

    @Test
    public void parsesLowercaseEnumValue() throws ValueParseException {
        Assertions.assertEquals(FilterableEnumValue.value(Example.THING), parser.parse("tHiNg"));
        Assertions.assertEquals(FilterableEnumValue.value(Example.ANOTHER), parser.parse("another"));
    }

    @Test
    public void parsesEnumValue() throws ValueParseException {
        Assertions.assertEquals(FilterableEnumValue.value(Example.THING), parser.parse("THING"));
        Assertions.assertEquals(FilterableEnumValue.value(Example.ANOTHER), parser.parse("ANOTHER"));
        Assertions.assertEquals(FilterableEnumValue.allValue().toString(), parser.parse("ALL").toString());
        Assertions.assertEquals(FilterableEnumValue.noneValue().toString(), parser.parse("NONE").toString());
    }
}