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
package com.synopsys.integration.configuration.property.types.path;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;
import static java.util.Collections.emptyList;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
public class PathPropertiesTest {
    @Test
    public void testNullable() throws InvalidPropertyException {
        final NullableProperty<PathValue> property = new NullablePathProperty("path.nullable");
        final PropertyConfiguration config = configOf(Pair.of("path.nullable", "/new/path"));
        Assertions.assertEquals(Optional.of(new PathValue("/new/path")), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        final ValuedProperty<PathValue> property = new PathProperty("path.valued", new PathValue("/tmp/test"));
        final PropertyConfiguration config = configOf(Pair.of("path.valued", "/new/path"));
        Assertions.assertEquals(new PathValue("/new/path"), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testList() throws InvalidPropertyException {
        final ValuedListProperty<PathValue> property = new PathListProperty("path.list", emptyList());
        final PropertyConfiguration config = configOf(Pair.of("path.list", "/new/path,/other/new/path"));
        Assertions.assertEquals(Bds.listOf(new PathValue("/new/path"), new PathValue("/other/new/path")), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}