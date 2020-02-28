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
package com.synopsys.integration.configuration.property.types.enumsoft;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.configuration.parse.ValueParseException;

class SoftEnumValueParserTest {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "Thing ", " THING" })
    public void unknownValues(final String expectedValue) throws ValueParseException {
        final SoftEnumValue<Example> resolvedValue = new SoftEnumValueParser<>(Example.class).parse(expectedValue);
        if (resolvedValue.getSoftValue().isPresent()) {
            Assertions.assertEquals(expectedValue, resolvedValue.getSoftValue().get(), "Should parse value to a soft string.");
        } else {
            fail("Should have resolved to an soft string and not an enum.");
        }
    }

    @Test
    public void parsesEnumValue() throws ValueParseException {
        assertValidSoftEnum(Example.THING, "THING");
        assertValidSoftEnum(Example.ANOTHER, "ANOTHER");
        assertValidSoftEnum(Example.THIRD, "THIRD");
    }

    private void assertValidSoftEnum(final Example expectedValue, final String rawValue) throws ValueParseException {
        final SoftEnumValue<Example> actualValue = new SoftEnumValueParser<>(Example.class).parse(rawValue);
        Assertions.assertEquals(SoftEnumValue.ofEnumValue(expectedValue), actualValue);
        Assertions.assertEquals(rawValue, actualValue.toString());
    }
}