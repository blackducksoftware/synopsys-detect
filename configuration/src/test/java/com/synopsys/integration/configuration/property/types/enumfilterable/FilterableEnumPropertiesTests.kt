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
package com.synopsys.integration.configuration.property.types.enumfilterable

import com.synopsys.integration.configuration.property.PropertyTestHelpUtil
import com.synopsys.integration.configuration.util.ConfigTestUtils.configOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class FilterableEnumPropertiesTests {
    private enum class Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    fun testNullable() {
        val property = NullableFilterableEnumProperty("enum.nullable", Example::class.java)
        val config = configOf("enum.nullable" to "NONE")

        val value = config.getValue(property).get();

        if (value.isAll) {
            fail("Expected type to be None instead of All.")
        } else if (value.value.isPresent) {
            fail("Expected type to be None instead of Value: ${value.value.get()}")
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD", "NONE", "ALL"))
    }

    @Test
    fun testValued() {
        val property = FilterableEnumProperty("enum.valued", FilterableEnumValue.allValue(), Example::class.java)
        val config = configOf("enum.valued" to "THIRD")

        val value = config.getValue(property);

        if (value.isNone) {
            fail("Expected type to be Value instead of None.")
        } else if (value.isAll) {
            fail("Expected type to be Value instead of All.")
        } else {
            Assertions.assertEquals(FilterableEnumValue.value(Example.THIRD), value);
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD", "NONE", "ALL"))
    }

    @Test
    fun testList() {
        val property = FilterableEnumListProperty("enum.list", listOf(FilterableEnumValue.value(Example.ANOTHER), FilterableEnumValue.value(Example.THING)), Example::class.java)
        val config = configOf("enum.valued" to "ANOTHER,THING")

        val value = config.getValue(property);

        if (FilterableEnumUtils.containsNone(value)) {
            fail("Expected type to be Value instead of None.")
        } else if (FilterableEnumUtils.containsAll(value)) {
            fail("Expected type to be Value instead of All.")
        } else {
            Assertions.assertEquals(listOf(FilterableEnumValue.value(Example.ANOTHER), FilterableEnumValue.value(Example.THING)), value)
        }
        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD", "NONE", "ALL"))
    }
}