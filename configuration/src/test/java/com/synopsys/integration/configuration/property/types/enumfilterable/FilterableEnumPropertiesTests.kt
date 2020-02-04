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
import com.synopsys.integration.configuration.util.configOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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

        when (val value = config.getValue(property)) {
            is Value -> Assertions.fail<NullableFilterableEnumProperty<Example>>("Expected type to be None instead of Value: ${value.value}")
            is All -> Assertions.fail<NullableFilterableEnumProperty<Example>>("Expected type to be None instead of All.")
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD", "NONE", "ALL"))
    }

    @Test
    fun testValued() {
        val property = FilterableEnumProperty("enum.valued", All(), Example::class.java)
        val config = configOf("enum.valued" to "THIRD")

        when (val value = config.getValue(property)) {
            is Value -> Assertions.assertEquals(Value(Example.THIRD), value)
            is All -> Assertions.fail<NullableFilterableEnumProperty<Example>>("Expected type to be Value instead of All.")
            is None -> Assertions.fail<NullableFilterableEnumProperty<Example>>("Expected type to be Value instead of None.")
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD", "NONE", "ALL"))
    }

    @Test
    fun testList() {
        val property = FilterableEnumListProperty("enum.list", listOf(Value(Example.ANOTHER), Value(Example.THING)), Example::class.java)
        val config = configOf("enum.valued" to "ANOTHER,THING")

        when (val value = config.getValue(property)) {
            is Value<*> -> Assertions.assertEquals(listOf(Value(Example.ANOTHER), Value(Example.THIRD)), value)
            is All<*> -> Assertions.fail<NullableFilterableEnumProperty<Example>>("Expected type to be Value instead of All.")
            is None<*> -> Assertions.fail<NullableFilterableEnumProperty<Example>>("Expected type to be Value instead of None.")
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD", "NONE", "ALL"))
    }
}