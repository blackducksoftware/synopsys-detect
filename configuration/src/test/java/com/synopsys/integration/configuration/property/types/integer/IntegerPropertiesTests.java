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
package com.synopsys.integration.configuration.property.types.integer;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.Collections;
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
public class IntegerPropertiesTests {
    @Test
    public void testNullable() throws InvalidPropertyException {
        final NullableProperty<Integer> property = new NullableIntegerProperty("integer.nullable");
        final PropertyConfiguration config = configOf(Pair.of("integer.nullable", "2"));
        Assertions.assertEquals(Optional.of(2), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        final ValuedProperty<Integer> property = new IntegerProperty("integer.valued", 2);
        final PropertyConfiguration config = configOf(Pair.of("integer.valued", "5"));
        Assertions.assertEquals(new Integer(5), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testList() throws InvalidPropertyException {
        final ValuedListProperty<Integer> property = new IntegerListProperty("integer.list", Collections.emptyList());
        final PropertyConfiguration config = configOf(Pair.of("integer.list", "2,3"));
        Assertions.assertEquals(Bds.listOf(2, 3), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}