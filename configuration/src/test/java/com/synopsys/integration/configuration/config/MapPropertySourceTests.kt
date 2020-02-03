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

import com.synopsys.integration.configuration.source.MapPropertySource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MapPropertySourceTests {
    @Test
    fun testNormalizesKeys() {
        val source = MapPropertySource("test", mapOf("CAPITAL_UNDERSCORE" to "value"))
        val keys = source.getKeys()
        Assertions.assertEquals(setOf("capital.underscore"), keys)
    }

    @Test
    fun returnsKey() {
        val source = MapPropertySource("test", mapOf("property.key" to "value"))
        Assertions.assertEquals("value", source.getValue("property.key"));
        Assertions.assertEquals("test", source.getOrigin("property.key"));
        Assertions.assertEquals("test", source.getName());
    }
}