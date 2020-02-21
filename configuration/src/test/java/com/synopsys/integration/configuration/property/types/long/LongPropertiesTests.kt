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
package com.synopsys.integration.configuration.property.types.long

import com.synopsys.integration.configuration.property.PropertyTestHelpUtil
import com.synopsys.integration.configuration.property.types.longs.LongListProperty
import com.synopsys.integration.configuration.property.types.longs.LongProperty
import com.synopsys.integration.configuration.property.types.longs.NullableLongProperty
import com.synopsys.integration.configuration.util.configOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class LongPropertiesTests {
    @Test
    fun testNullable() {
        val property = NullableLongProperty("long.nullable")
        val config = configOf("long.nullable" to "2")
        Assertions.assertEquals(Optional.of(2L), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property)
    }

    @Test
    fun testValued() {
        val property = LongProperty("long.valued", 2L)
        val config = configOf("long.valued" to "5")
        Assertions.assertEquals(5L, config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property)
    }

    @Test
    fun testList() {
        val property = LongListProperty("long.list", emptyList())
        val config = configOf("long.list" to "2,3")
        Assertions.assertEquals(listOf(2L, 3L), config.getValue(property))

        PropertyTestHelpUtil.assertAllHelpValid(property)
    }
}