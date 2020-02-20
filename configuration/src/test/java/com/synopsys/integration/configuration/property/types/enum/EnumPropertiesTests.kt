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

import com.synopsys.integration.configuration.property.PropertyTestHelpUtil
import com.synopsys.integration.configuration.property.types.enums.EnumListProperty
import com.synopsys.integration.configuration.property.types.enums.EnumProperty
import com.synopsys.integration.configuration.property.types.enums.NullableEnumProperty
import com.synopsys.integration.configuration.util.ConfigTestUtils.configOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class EnumPropertiesTests {
    private enum class Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    fun testExampleValues() {
        val property = NullableEnumProperty("example.list", Example::class.java)
        Assertions.assertEquals(listOf("THING", "ANOTHER", "THIRD"), property.listExampleValues());
    }

    @Test
    fun testNullable() {
        val property = NullableEnumProperty("enum.nullable", Example::class.java)
        val config = configOf("enum.nullable" to "ANOTHER")
        Assertions.assertEquals(Optional.of(Example.ANOTHER), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD"))
    }

    @Test
    fun testValued() {
        val property = EnumProperty("enum.valued", Example.THIRD, Example::class.java)
        val config = configOf("enum.valued" to "THIRD")
        Assertions.assertEquals(Example.THIRD, config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD"))
    }

    @Test
    fun testList() {
        val property = EnumListProperty("enum.list", listOf(Example.THIRD), Example::class.java)
        val config = configOf("enum.list" to "ANOTHER,THING")
        Assertions.assertEquals(listOf(Example.ANOTHER, Example.THING), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property)
    }
}