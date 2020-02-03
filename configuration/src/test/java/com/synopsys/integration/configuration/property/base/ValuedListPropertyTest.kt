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
package com.synopsys.integration.configuration.property.base

import com.synopsys.integration.configuration.parse.ListValueParser
import com.synopsys.integration.configuration.parse.ValueParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ValuedListPropertyTest {
    class TestValueParser : ValueParser<String>() {
        override fun parse(value: String): String {
            return value
        }
    }

    class TestListProperty(key: String, default: List<String>) : ValuedListProperty<String>(key, ListValueParser(TestValueParser()), default)

    @Test
    fun describeDefault() {
        val testProperty = TestListProperty("key", emptyList())
        Assertions.assertNotNull(testProperty.describeDefault(), "The default value description should be set.")
    }

    @Test
    fun isCommaSeparated() {
        val testProperty = TestListProperty("key", emptyList())
        Assertions.assertTrue(testProperty.isCommaSeparated(), "This parser should separate on comma and should report that it does.")
    }
}