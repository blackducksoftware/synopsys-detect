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
package com.synopsys.integration.configuration.util;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;

public class ConfigTestUtils {
    @NotNull
    public static PropertyConfiguration emptyConfig() {
        return new PropertyConfiguration(emptyList());
    }

    @SafeVarargs
    @NotNull
    public static PropertyConfiguration configOf(@NotNull final Pair<String, String>... properties) {
        return configOf(propertySourceOf("map", properties));
    }

    @SafeVarargs
    @NotNull
    public static PropertySource propertySourceOf(@NotNull final String name, @NotNull final Map.Entry<String, String>... properties) {
        return new MapPropertySource(name, Bds.mapOfEntries(properties));
    }

    @NotNull
    public static PropertyConfiguration configOf(@NotNull final Map<String, String> properties) {
        final PropertySource propertySource = new MapPropertySource("map", properties);
        return configOf(propertySource);
    }

    @NotNull
    public static PropertyConfiguration configOf(@NotNull final PropertySource... propertySources) {
        return new PropertyConfiguration(Arrays.asList(propertySources));
    }
}