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
package com.synopsys.integration.configuration.property.types.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.configuration.parse.ValueParseException;

public class EnumValueParserTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    private final EnumValueParser<Example> parser = new EnumValueParser<>(Example.class);

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "Thing ", " THING", "tHiNg" })
    public void unknownValues(final String value) {
        Assertions.assertThrows(ValueParseException.class, () -> parser.parse(value));
    }

    @Test
    public void parsesEnumValue() throws ValueParseException {
        Assertions.assertEquals(Example.THING, parser.parse("THING"));
        Assertions.assertEquals(Example.ANOTHER, parser.parse("ANOTHER"));
        Assertions.assertEquals(Example.THIRD, parser.parse("THIRD"));
    }
}