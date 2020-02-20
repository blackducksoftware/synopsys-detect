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
package com.synopsys.integration.configuration.property.types.enumsoft

import com.synopsys.integration.configuration.property.PropertyTestHelpUtil
import com.synopsys.integration.configuration.util.ConfigTestUtils.configOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class SoftEnumPropertiesTest {
    private enum class Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    fun testNullableActualValue() {
        val property = NullableSoftEnumProperty("enum.nullable", Example::class.java)
        val config = configOf("enum.nullable" to "ANOTHER")
        Assertions.assertEquals(Optional.of(SoftEnumValue.ofEnumValue(Example.ANOTHER)), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD"))
    }

    @Test
    fun testNullableStringValue() {
        val property = NullableSoftEnumProperty("enum.nullable", Example::class.java)
        val config = configOf("enum.nullable" to "ANOTHER ONE")
        Assertions.assertEquals(Optional.of(SoftEnumValue.ofSoftValue<Example>("ANOTHER ONE")), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD"))
    }


    @Test
    fun testValuedActualValue() {
        val property = SoftEnumProperty("enum.valued", SoftEnumValue.ofEnumValue(Example.ANOTHER), Example::class.java)
        val config = configOf("enum.valued" to "THIRD")
        Assertions.assertEquals(SoftEnumValue.ofEnumValue(Example.THIRD), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD"))
    }

    @Test
    fun testValuedStringValue() {
        val property = SoftEnumProperty("enum.valued", SoftEnumValue.ofEnumValue(Example.ANOTHER), Example::class.java)
        val config = configOf("enum.valued" to "THIRD ONE")
        Assertions.assertEquals(SoftEnumValue.ofSoftValue<Example>("THIRD ONE"), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property, expectedExampleValues = listOf("THING", "ANOTHER", "THIRD"))
    }

    @Test
    fun testList() {
        val property = SoftEnumListProperty("enum.list", listOf(SoftEnumValue.ofEnumValue(Example.THIRD)), Example::class.java)
        val config = configOf("enum.list" to "ANOTHER,THING,test")
        Assertions.assertEquals(listOf(SoftEnumValue.ofEnumValue(Example.ANOTHER), SoftEnumValue.ofEnumValue(Example.THING), SoftEnumValue.ofSoftValue<Example>("test")), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property)
    }
}