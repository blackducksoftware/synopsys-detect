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
package com.synopsys.integration.configuration.config;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;
import static com.synopsys.integration.configuration.util.ConfigTestUtils.propertySourceOf;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.types.string.NullableStringProperty;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.configuration.util.Bds;

class MultiplePropertySourceTests {
    @Test
    public void primaryValueUsedOverSecondary() throws InvalidPropertyException {
        final NullableProperty<String> sharedProperty = new NullableStringProperty("shared.key");
        final PropertySource secondarySource = propertySourceOf("secondaryName", Pair.of(sharedProperty.getKey(), "secondaryValue"));
        final PropertySource primarySource = propertySourceOf("primaryName", Pair.of(sharedProperty.getKey(), "primaryValue"));

        final PropertyConfiguration config = configOf(primarySource, secondarySource);
        Assertions.assertEquals(Optional.of("primaryValue"), config.getValue(sharedProperty));
        Assertions.assertEquals(Optional.of("primaryName"), config.getPropertySource(sharedProperty));
    }

    @Test
    public void valueFromSecondaryWhenNotInPrimary() throws InvalidPropertyException {
        final NullableProperty<String> property = new NullableStringProperty("any.key");
        final PropertySource secondarySource = propertySourceOf("secondaryName", Pair.of(property.getKey(), "secondaryValue"));
        final PropertySource primarySource = propertySourceOf("primaryName");

        final PropertyConfiguration config = configOf(primarySource, secondarySource);
        Assertions.assertEquals(Optional.of("secondaryValue"), config.getValue(property));
        Assertions.assertEquals(Optional.of("secondaryName"), config.getPropertySource(property));
    }

    @Test
    public void containsKeysFromBothSources() {
        final NullableProperty<String> primaryProperty = new NullableStringProperty("primary.key");
        final PropertySource primarySource = propertySourceOf("primaryName", Pair.of(primaryProperty.getKey(), "primaryValue"));

        final NullableProperty<String> secondaryProperty = new NullableStringProperty("secondary.key");
        final PropertySource secondarySource = propertySourceOf("secondaryName", Pair.of(secondaryProperty.getKey(), "secondaryValue"));

        final PropertyConfiguration config = configOf(primarySource, secondarySource);
        Assertions.assertEquals(Bds.setOf(primaryProperty.getKey(), secondaryProperty.getKey()), config.getKeys());
    }

    @Test
    public void rawValueMapContainsValuesFromBothSources() {
        final NullableProperty<String> primaryProperty = new NullableStringProperty("primary.key");
        final PropertySource primarySource = propertySourceOf("primaryName", Pair.of(primaryProperty.getKey(), "primaryValue"));

        final NullableProperty<String> secondaryProperty = new NullableStringProperty("secondary.key");
        final PropertySource secondarySource = propertySourceOf("secondaryName", Pair.of(secondaryProperty.getKey(), "secondaryValue"));

        final PropertyConfiguration config = configOf(primarySource, secondarySource);
        Assertions.assertEquals(Bds.mapOf(
            Pair.of(primaryProperty.getKey(), "primaryValue"),
            Pair.of(secondaryProperty.getKey(), "secondaryValue")
        ), config.getRaw());
    }
}