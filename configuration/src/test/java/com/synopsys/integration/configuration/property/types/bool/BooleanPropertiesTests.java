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
package com.synopsys.integration.configuration.property.types.bool;

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
class BooleanPropertiesTests {
    @Test
    public void testNullable() throws InvalidPropertyException {
        final NullableProperty<Boolean> property = new NullableBooleanProperty("boolean.nullable");
        final PropertyConfiguration config = configOf(Pair.of("boolean.nullable", "true"));
        Assertions.assertEquals(Optional.of(true), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);

    }

    @Test
    public void testValued() throws InvalidPropertyException {
        final ValuedProperty<Boolean> property = new BooleanProperty("boolean.valued", false);
        final PropertyConfiguration config = configOf(Pair.of("boolean.valued", "true"));
        Assertions.assertEquals(true, config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testList() throws InvalidPropertyException {
        final ValuedListProperty<Boolean> property = new BooleanListProperty("boolean.list", Collections.emptyList());
        final PropertyConfiguration config = configOf(Pair.of("boolean.list", "true, true"));
        Assertions.assertEquals(Bds.listOf(true, true), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }
}