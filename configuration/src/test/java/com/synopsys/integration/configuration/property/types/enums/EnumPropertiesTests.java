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
package com.synopsys.integration.configuration.property.types.enums;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;
import com.synopsys.integration.configuration.util.Bds;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
public class EnumPropertiesTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    public void testExampleValues() {
        final NullableEnumProperty<Example> property = new NullableEnumProperty<>("example.list", Example.class);
        Assertions.assertEquals(Bds.listOf("THING", "ANOTHER", "THIRD"), property.listExampleValues());
    }

    @Test
    public void testNullable() throws InvalidPropertyException {
        final NullableEnumProperty<Example> property = new NullableEnumProperty<>("enum.nullable", Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.nullable", "ANOTHER"));
        Assertions.assertEquals(Optional.of(Example.ANOTHER), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        final EnumProperty<Example> property = new EnumProperty<>("enum.valued", Example.THIRD, Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD"));
        Assertions.assertEquals(Example.THIRD, config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testList() throws InvalidPropertyException {
        final EnumListProperty<Example> property = new EnumListProperty<>("enum.list", Bds.listOf(Example.THIRD), Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.list", "ANOTHER,THING"));
        Assertions.assertEquals(Bds.listOf(Example.ANOTHER, Example.THING), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}