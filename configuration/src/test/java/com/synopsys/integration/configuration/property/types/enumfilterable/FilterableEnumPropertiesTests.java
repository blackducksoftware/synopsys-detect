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
package com.synopsys.integration.configuration.property.types.enumfilterable;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class FilterableEnumPropertiesTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    public void testNullable() throws InvalidPropertyException {
        final NullableFilterableEnumProperty<Example> property = new NullableFilterableEnumProperty<>("enum.nullable", Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.nullable", "NONE"));

        final FilterableEnumValue<Example> value = config.getValue(property).get();

        if (value.isAll()) {
            fail("Expected type to be None instead of All.");
        } else if (value.getValue().isPresent()) {
            fail("Expected type to be None instead of Value: ${value.value.get()}");
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD", "NONE", "ALL"));
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        final FilterableEnumProperty<Example> property = new FilterableEnumProperty<>("enum.valued", FilterableEnumValue.allValue(), Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD"));

        final FilterableEnumValue<Example> value = config.getValue(property);

        if (value.isNone()) {
            fail("Expected type to be Value instead of None.");
        } else if (value.isAll()) {
            fail("Expected type to be Value instead of All.");
        } else {
            Assertions.assertEquals(FilterableEnumValue.value(Example.THIRD), value);
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD", "NONE", "ALL"));
    }

    @Test
    public void testList() throws InvalidPropertyException {
        final FilterableEnumListProperty<Example> property = new FilterableEnumListProperty<>("enum.list", Arrays.asList(FilterableEnumValue.value(Example.ANOTHER), FilterableEnumValue.value(Example.THING)), Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.valued", "ANOTHER,THING"));

        final List<FilterableEnumValue<Example>> value = config.getValue(property).toFilterableValues();

        if (FilterableEnumUtils.containsNone(value)) {
            fail("Expected type to be Value instead of None.");
        } else if (FilterableEnumUtils.containsAll(value)) {
            fail("Expected type to be Value instead of All.");
        } else {
            Assertions.assertEquals(Arrays.asList(FilterableEnumValue.value(Example.ANOTHER), FilterableEnumValue.value(Example.THING)), value);
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD", "NONE", "ALL"));
    }
}