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
package com.synopsys.integration.configuration.property.types.enum

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.property.types.enums.EnumValueParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class EnumValueParserTests {
    enum class Example {
        THING,
        ANOTHER,
        THIRD
    }

    @ParameterizedTest
    @ValueSource(strings = ["unknown", "Thing ", " THING", "tHiNg"])
    fun unknownValues(value: String) {
        Assertions.assertThrows(ValueParseException::class.java) {
            EnumValueParser(Example::class.java).parse(value)
        }
    }

    @Test
    fun parsesEnumValue() {
        Assertions.assertEquals(Example.THING, EnumValueParser(Example::class.java).parse("THING"))
        Assertions.assertEquals(Example.ANOTHER, EnumValueParser(Example::class.java).parse("ANOTHER"))
        Assertions.assertEquals(Example.THIRD, EnumValueParser(Example::class.java).parse("THIRD"))
    }
}