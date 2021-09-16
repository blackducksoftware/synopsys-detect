/**
 * synopsys-detect
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
package com.synopsys.integration.detect.configuration;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;

class DetectConfigurationFactoryTestUtils {
    @SafeVarargs
    public static DetectConfigurationFactory spyFactoryOf(final Pair<Property, String>... properties) {
        return Mockito.spy(factoryOf(properties));
    }

    @SafeVarargs
    public static DetectConfigurationFactory factoryOf(final Pair<Property, String>... properties) {
        final Map<String, String> propertyMap = Bds.of(properties).toMap(pair -> pair.getLeft().getKey(), Pair::getRight);
        final PropertySource inMemoryPropertySource = new MapPropertySource("test", propertyMap);
        final PropertyConfiguration propertyConfiguration = new PropertyConfiguration(Collections.singletonList(inMemoryPropertySource));

        return new DetectConfigurationFactory(propertyConfiguration, new SimplePathResolver(), new Gson());
    }
}