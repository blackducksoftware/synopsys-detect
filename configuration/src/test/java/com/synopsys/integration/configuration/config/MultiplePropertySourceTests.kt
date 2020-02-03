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
package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.property.types.string.NullableStringProperty
import com.synopsys.integration.configuration.util.configOf
import com.synopsys.integration.configuration.util.propertySourceOf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MultiplePropertySourceTests {
    @Test
    fun primaryValueUsedOverSecondary() {
        val sharedProperty = NullableStringProperty("shared.key")
        val secondarySource = propertySourceOf("secondaryName", sharedProperty.key to "secondaryValue")
        val primarySource = propertySourceOf("primaryName", sharedProperty.key to "primaryValue")

        val config = configOf(primarySource, secondarySource);
        Assertions.assertEquals("primaryValue", config.getValue(sharedProperty))
        Assertions.assertEquals("primaryName", config.getPropertySource(sharedProperty))
    }

    @Test
    fun valueFromSecondaryWhenNotInPrimary() {
        val property = NullableStringProperty("any.key")
        val secondarySource = propertySourceOf("secondaryName", property.key to "secondaryValue")
        val primarySource = propertySourceOf("primaryName")

        val config = configOf(primarySource, secondarySource)
        Assertions.assertEquals("secondaryValue", config.getValue(property))
        Assertions.assertEquals("secondaryName", config.getPropertySource(property))
    }

    @Test
    fun containsKeysFromBothSources() {
        val primaryProperty = NullableStringProperty("primary.key")
        val primarySource = propertySourceOf("primaryName", primaryProperty.key to "primaryValue")

        val secondaryProperty = NullableStringProperty("secondary.key")
        val secondarySource = propertySourceOf("secondaryName", secondaryProperty.key to "secondaryValue")

        val config = configOf(primarySource, secondarySource)
        Assertions.assertEquals(setOf(primaryProperty.key, secondaryProperty.key), config.getKeys())
    }

    @Test
    fun rawValueMapContainsValuesFromBothSources() {
        val primaryProperty = NullableStringProperty("primary.key")
        val primarySource = propertySourceOf("primaryName", primaryProperty.key to "primaryValue")

        val secondaryProperty = NullableStringProperty("secondary.key")
        val secondarySource = propertySourceOf("secondaryName", secondaryProperty.key to "secondaryValue")

        val config = configOf(primarySource, secondarySource);
        Assertions.assertEquals(mapOf(
                primaryProperty.key to "primaryValue",
                secondaryProperty.key to "secondaryValue"
        ), config.getRaw())
    }
}