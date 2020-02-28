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
package com.synopsys.integration.configuration.property.types.enumextended;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.util.Bds;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
public class ExtendedEnumPropertiesTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    private enum ExampleExtension {
        NONE
    }

    @Test
    public void testNullable() throws InvalidPropertyException {
        final NullableProperty<ExtendedEnumValue<ExampleExtension, Example>> property = new NullableExtendedEnumProperty<>("enum.nullable", ExampleExtension.class, Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.nullable", "NONE"));
        final Optional<ExtendedEnumValue<ExampleExtension, Example>> value = config.getValue(property);
        Assertions.assertEquals(Optional.of(ExtendedEnumValue.ofExtendedValue(ExampleExtension.NONE)), value);

        PropertyTestHelpUtil.assertAllHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD", "NONE"));
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        final ValuedProperty<ExtendedEnumValue<ExampleExtension, Example>> property = new ExtendedEnumProperty<>("enum.nullable", ExtendedEnumValue.ofExtendedValue(ExampleExtension.NONE), ExampleExtension.class, Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.nullable", "ANOTHER"));
        final ExtendedEnumValue<ExampleExtension, Example> value = config.getValue(property);
        Assertions.assertEquals(ExtendedEnumValue.ofBaseValue(Example.ANOTHER), value);

        PropertyTestHelpUtil.assertAllHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD", "NONE"));
    }

    @Test
    public void testList() throws InvalidPropertyException {
        final List<ExtendedEnumValue<ExampleExtension, Example>> defaultValue = Bds.listOf(ExtendedEnumValue.ofBaseValue(Example.THING), ExtendedEnumValue.ofExtendedValue(ExampleExtension.NONE));
        final ValuedListProperty<ExtendedEnumValue<ExampleExtension, Example>> property = new ExtendedEnumListProperty<>("enum.nullable", defaultValue, ExampleExtension.class, Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.nullable", "THIRD,NONE"));
        final List<ExtendedEnumValue<ExampleExtension, Example>> value = config.getValue(property);
        Assertions.assertEquals(Bds.listOf(ExtendedEnumValue.ofBaseValue(Example.THIRD), ExtendedEnumValue.ofExtendedValue(ExampleExtension.NONE)), value);

        PropertyTestHelpUtil.assertAllListHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD", "NONE"));
    }
}