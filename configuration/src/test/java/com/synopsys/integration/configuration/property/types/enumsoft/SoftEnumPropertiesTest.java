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
package com.synopsys.integration.configuration.property.types.enumsoft;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class SoftEnumPropertiesTest {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    public void testNullableActualValue() throws InvalidPropertyException {
        final NullableSoftEnumProperty<Example> property = new NullableSoftEnumProperty<>("enum.nullable", Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.nullable", "ANOTHER"));
        Assertions.assertEquals(Optional.of(SoftEnumValue.ofEnumValue(Example.ANOTHER)), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testNullableStringValue() throws InvalidPropertyException {
        final NullableSoftEnumProperty<Example> property = new NullableSoftEnumProperty<>("enum.nullable", Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.nullable", "ANOTHER ONE"));
        Assertions.assertEquals(Optional.of(SoftEnumValue.ofSoftValue("ANOTHER ONE")), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testValuedActualValue() throws InvalidPropertyException {
        final SoftEnumProperty<Example> property = new SoftEnumProperty<>("enum.valued", SoftEnumValue.ofEnumValue(Example.ANOTHER), Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD"));
        Assertions.assertEquals(SoftEnumValue.ofEnumValue(Example.THIRD), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testValuedStringValue() throws InvalidPropertyException {
        final SoftEnumProperty<Example> property = new SoftEnumProperty<>("enum.valued", SoftEnumValue.ofEnumValue(Example.ANOTHER), Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD ONE"));
        Assertions.assertEquals(SoftEnumValue.ofSoftValue("THIRD ONE"), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testList() throws InvalidPropertyException {
        final SoftEnumListProperty<Example> property = new SoftEnumListProperty<>("enum.list", Collections.singletonList(SoftEnumValue.ofEnumValue(Example.THIRD)), Example.class);
        final PropertyConfiguration config = configOf(Pair.of("enum.list", "ANOTHER,THING,test"));
        Assertions.assertEquals(Arrays.asList(SoftEnumValue.ofEnumValue(Example.ANOTHER), SoftEnumValue.ofEnumValue(Example.THING), SoftEnumValue.ofSoftValue("test")), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}