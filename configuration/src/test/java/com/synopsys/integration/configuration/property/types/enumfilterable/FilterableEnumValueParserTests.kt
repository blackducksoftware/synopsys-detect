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
package com.synopsys.integration.configuration.property.types.enumextended

import com.synopsys.integration.configuration.parse.ValueParseException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FilterableEnumValueParserTests {
    private enum class Example {
        THING,
        ANOTHER,
        THIRD
    }

    private enum class ExampleExtended {
        FOURTH
    }

    @ParameterizedTest
    @ValueSource(strings = ["unknown", "Thing ", " THING", "tHiNg", "fourth"])
    fun unknownValues(value: String) {
        Assertions.assertThrows(ValueParseException::class.java) {
            ExtendedEnumValueParser(ExampleExtended::class.java, Example::class.java).parse(value)
        }
    }

    @Test
    fun parsesEnumValue() {
        Assertions.assertEquals(BaseValue<ExampleExtended, Example>(Example.THING), ExtendedEnumValueParser(ExampleExtended::class.java, Example::class.java).parse("THING"))
        Assertions.assertEquals(BaseValue<ExampleExtended, Example>(Example.ANOTHER), ExtendedEnumValueParser(ExampleExtended::class.java, Example::class.java).parse("ANOTHER"))
        Assertions.assertEquals(BaseValue<ExampleExtended, Example>(Example.THIRD), ExtendedEnumValueParser(ExampleExtended::class.java, Example::class.java).parse("THIRD"))
        Assertions.assertEquals(ExtendedValue<ExampleExtended, Example>(ExampleExtended.FOURTH), ExtendedEnumValueParser(ExampleExtended::class.java, Example::class.java).parse("FOURTH"))
    }
}