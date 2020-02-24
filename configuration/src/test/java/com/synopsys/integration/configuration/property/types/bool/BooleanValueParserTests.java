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
package com.synopsys.integration.configuration.property.types.bool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.configuration.parse.ValueParseException;

class BooleanValueParserTests {
    @ParameterizedTest
    @ValueSource(strings = { "unknown", "we ird tef ", "243354323", "@Q@ASD" })
    public void parseUnknownThrows(final String value) {
        Assertions.assertThrows(ValueParseException.class, () -> new BooleanValueParser().parse(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "tRuE", "true ", " true", "    ", "", "t" })
    public void booleanParsesTrue(final String value) throws ValueParseException {
        Assertions.assertTrue(new BooleanValueParser().parse(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "false", "f" })
    public void booleanParsesFalse(final String value) throws ValueParseException {
        Assertions.assertFalse(new BooleanValueParser().parse(value));
    }
}