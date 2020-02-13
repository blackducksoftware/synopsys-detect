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

import com.synopsys.integration.configuration.property.PropertyTestHelpUtil
import com.synopsys.integration.configuration.util.configOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class ExtendedEnumPropertiesTests {
    private enum class Example {
        THING,
        ANOTHER,
        THIRD
    }

    private enum class ExampleExtension {
        NONE
    }

    @Test
    fun testNullable() {
        val property = NullableExtendedEnumProperty("enum.nullable", ExampleExtension::class.java, Example::class.java)
        val config = configOf("enum.nullable" to "NONE")
        val value = config.getValue(property)
        Assertions.assertEquals(ExtendedEnumValue.ofExtendedValue<ExampleExtension, Example>(ExampleExtension.NONE), value)

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD", "NONE"))
    }

    @Test
    fun testValued() {
        val property = ExtendedEnumProperty<ExampleExtension, Example>("enum.nullable", ExtendedEnumValue.ofExtendedValue<ExampleExtension, Example>(ExampleExtension.NONE), ExampleExtension::class.java, Example::class.java)
        val config = configOf("enum.nullable" to "ANOTHER")
        val value = config.getValue(property)
        Assertions.assertEquals(ExtendedEnumValue.ofBaseValue<ExampleExtension, Example>(Example.ANOTHER), value)

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD", "NONE"))
    }

    @Test
    fun testList() {
        val defaultValue = listOf(ExtendedEnumValue.ofBaseValue<ExampleExtension, Example>(Example.THING), ExtendedEnumValue.ofExtendedValue<ExampleExtension, Example>(ExampleExtension.NONE))
        val property = ExtendedEnumListProperty("enum.nullable", defaultValue, ExampleExtension::class.java, Example::class.java)
        val config = configOf("enum.nullable" to "THIRD,NONE")
        val value = config.getValue(property)
        Assertions.assertEquals(listOf(ExtendedEnumValue.ofBaseValue<ExampleExtension, Example>(Example.THIRD), ExtendedEnumValue.ofExtendedValue<ExampleExtension, Example>(ExampleExtension.NONE)), value)

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD", "NONE"))
    }
}