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
import com.synopsys.integration.configuration.source.SpringConfigurationPropertySource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.mock.env.MockEnvironment
import java.util.*

class SpringConfigurationPropertySourceTests {
    @Test
    fun verifySpringReturnsValue() {
        val m = MockEnvironment()
        m.setProperty("example.key", "value")

        val sources = SpringConfigurationPropertySource.fromConfigurableEnvironment(m)
        val config = PropertyConfiguration(sources)

        val property = NullableStringProperty("example.key")
        Assertions.assertEquals(Optional.of("value"), config.getValue(property))
        Assertions.assertEquals(Optional.of("mockProperties"), config.getPropertySource(property))
    }
}