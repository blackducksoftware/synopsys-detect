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
package com.synopsys.integration.configuration.property.types.integer

import com.synopsys.integration.configuration.property.PropertyTestHelpUtil
import com.synopsys.integration.configuration.util.configOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class IntegerPropertiesTests {
    @Test
    fun testNullable() {
        val property = NullableIntegerProperty("integer.nullable")
        val config = configOf("integer.nullable" to "2")
        Assertions.assertEquals(Optional.of(2), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property)
    }

    @Test
    fun testValued() {
        val property = IntegerProperty("integer.valued", 2)
        val config = configOf("integer.valued" to "5")
        Assertions.assertEquals(5, config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property)
    }

    @Test
    fun testList() {
        val property = IntegerListProperty("integer.list", emptyList())
        val config = configOf("integer.list" to "2,3")
        Assertions.assertEquals(listOf(2, 3), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property)
    }
}