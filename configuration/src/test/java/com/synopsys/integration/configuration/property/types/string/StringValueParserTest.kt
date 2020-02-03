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
package com.synopsys.integration.configuration.property.types.string

import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class StringValueParserTest {
    @Test
    fun parseValid() {
        Assertions.assertEquals("any string should work", StringValueParser().parse("any string should work"))
    }

    @RepeatedTest(10)
    fun parseRandom() {
        Assertions.assertDoesNotThrow {
            val randomString = RandomStringUtils.random(20, true, true)
            println("Testing with random string '$randomString'")
            StringValueParser().parse(randomString)
        }
    }
}